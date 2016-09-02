/*
 * Copyright 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.kinesis.datavis.kcl.processor;

import com.amazonaws.services.kinesis.clientlibrary.exceptions.InvalidStateException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ShutdownException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ThrottlingException;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownReason;
import com.amazonaws.services.kinesis.model.Record;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdbc.dao.MappingDAO;
import com.jdbc.vo.Mapping;
import com.kinesis.datavis.kcl.counter.SlidingWindowCounter;
import com.kinesis.datavis.kcl.persistence.CountPersister;
import com.kinesis.datavis.kcl.timing.Clock;
import com.kinesis.datavis.kcl.timing.NanoClock;
import com.kinesis.datavis.kcl.timing.Timer;
import com.kinesis.datavis.utils.ReflectionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Computes a map of (HttpReferrerPair -> count(pair)) over a fixed range of time. Counts are computed at the intervals
 * provided.
 *
 * @param <T> The type of records this processor is capable of counting.
 */
public class CountingRecordProcessor<T,C> implements IRecordProcessor {
    private static final Log LOG = LogFactory.getLog(CountingRecordProcessor.class);

    // Lock to use for our timer
    private static final Clock NANO_CLOCK = new NanoClock();
    // The timer to schedule checkpoints with
    private Timer checkpointTimer = new Timer(NANO_CLOCK);

    // Our JSON object mapper for deserializing records
    private final ObjectMapper JSON;

    // Interval to calculate distinct counts across
    private int computeIntervalInMillis;
    // Total range to consider counts when calculating totals
    private int computeRangeInMillis;

    // Counter for keeping track of counts per interval.
    private SlidingWindowCounter<T> windowCounter;

    // The shard this processor is processing
    private String kinesisShardId;

    // We schedule count updates at a fixed rate (computeIntervalInMillis) on a separate thread
    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

    // This is responsible for persisting our counts every interval
    private CountPersister<T,C> persister;

    private MappingDAO mappingDAO;

    private CountingRecordProcessorConfig config;

    // The type of record we expect to receive as JSON
    private Class<T> recordType;

    /**
     * Create a new processor.
     *
     * @param config Configuration for this record processor.
     * @param recordType The type of record we expect to receive as a UTF-8 JSON string.
     * @param persister Counts will be persisted with this persister.
     * @param computeRangeInMillis Range to compute distinct counts across
     * @param computeIntervalInMillis Interval between computing total count for the overall time range.
     */
    public CountingRecordProcessor(CountingRecordProcessorConfig config,
            Class<T> recordType,
            CountPersister<T, C> persister,
                                   MappingDAO mappingDAO,
            int computeRangeInMillis,
            int computeIntervalInMillis) {

        this.config = config;
        this.recordType = recordType;
        this.persister = persister;
        this.mappingDAO = mappingDAO;
        this.computeRangeInMillis = computeRangeInMillis;
        this.computeIntervalInMillis = computeIntervalInMillis;

        // Create an object mapper to deserialize records that ignores unknown properties
        JSON = new ObjectMapper();
        JSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void initialize(String shardId) {
        kinesisShardId = shardId;

        resetCheckpointAlarm();

        persister.initialize();

        // Create a sliding window whose size is large enough to hold an entire range of individual interval counts.
        windowCounter = new SlidingWindowCounter<>((int) (computeRangeInMillis / computeIntervalInMillis));

        // Create a scheduled task that runs every computeIntervalInMillis to compute and
        // persistCounter the counts.
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // Synchronize on the windowCounter so we stop advancing the interval while we're checkpointing
                synchronized (windowCounter) {
                    try {
                        advanceOneInterval();
                    } catch (Exception ex) {
                        LOG.warn("Error advancing sliding window one interval (" + computeIntervalInMillis
                                + "ms). Skipping this interval.", ex);
                    }
                }
            }
        },
                TimeUnit.SECONDS.toMillis(config.getInitialWindowAdvanceDelayInSeconds()), computeIntervalInMillis,
                TimeUnit.MILLISECONDS);
    }

    /**
     * Advance the internal sliding window windowCounter one interval. This will invoke our count persister if the window is
     * full.
     */
    protected void advanceOneInterval() {
        Map<T, Long> counts = null;
        synchronized (windowCounter) {
            // Only persist the counts if we have a full range of data to report. We don't want partial
            // counts each time the process starts.
            if (shouldPersistCounts()) {
                counts = windowCounter.getCounts();

                windowCounter.pruneEmptyObjects();
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format("We have not collected enough interval samples to calculate across the "
                            + "entire range from shard %s. Skipping this interval.", kinesisShardId));
                }
            }
            // Advance the window "1 tick"
            windowCounter.advanceWindow();
        }
        // Persist the counts if we have a full range
        if (counts != null) {

            persister.persistCounter(counts);
        }
    }

    @Override
    public void processRecords(List<Record> records, IRecordProcessorCheckpointer checkpointer) {
        for (Record r : records) {
            // Deserialize each record as an UTF-8 encoded JSON String of the type provided
            T rec;
            try {
                rec = JSON.readValue(r.getData().array(), recordType);


                Mapping mapping = mappingDAO.load(ReflectionUtil.getValue(rec, "getBidRequestId"));

                ReflectionUtil.setValue(rec, "bannerId", mapping.getBannerId());
                ReflectionUtil.setValue(rec, "audienceId", mapping.getAudienceId());

            } catch (IOException e) {
                LOG.warn("Skipping record. Unable to parse record into HttpReferrerPair. Partition Key: "
                        + r.getPartitionKey() + ". Sequence Number: " + r.getSequenceNumber(),
                        e);
                continue;
            }
            // Increment the windowCounter for the new pair. This is synchronized because there is another thread reading from
            // the windowCounter to compute running totals every interval.
            synchronized (windowCounter) {
                windowCounter.increment(rec);
            }
        }

        // Checkpoint if it's time to!
        if (checkpointTimer.isTimeUp()) {
            // Obtain a lock on the windowCounter to prevent additional counts from being calculated while checkpointing.
            synchronized (windowCounter) {
                checkpoint(checkpointer);
                resetCheckpointAlarm();
            }
        }
    }

    /**
     * We must have collected a full range window worth of samples before we should persistCounter any counts.
     *
     * @return {@code true} if we've collected enough samples to persistCounter a complete count for the entire range.
     */
    private boolean shouldPersistCounts() {
        return windowCounter.isWindowFull();
    }

    @Override
    public void shutdown(IRecordProcessorCheckpointer checkpointer, ShutdownReason reason) {
        LOG.info("Shutting down record processor for shard: " + kinesisShardId);

        scheduledExecutor.shutdown();
        try {
            // Wait for at most 30 seconds for the executor service's tasks to complete
            if (!scheduledExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                LOG.warn("Failed to properly shut down interval thread pool for calculating interval counts and persisting them. Some counts may not have been persisted.");
            } else {
                // Only checkpoint if we successfully shut down the thread pool
                // Important to checkpoint after reaching end of shard, so we can start processing data from child
                // shards.
                if (reason == ShutdownReason.TERMINATE) {
                    synchronized (windowCounter) {
                        checkpoint(checkpointer);
                    }
                }
            }
        } catch (InterruptedException ie) {
            // We failed to shutdown cleanly, do not checkpoint.
            scheduledExecutor.shutdownNow();
            // Handle this similar to a host or process crashing and abort the JVM.
            LOG.fatal("Couldn't successfully persistCounter data within the max wait time. Aborting the JVM to mimic a crash.");
            System.exit(1);
        }
    }

    /**
     * Set the timer for the next checkpoint.
     */
    private void resetCheckpointAlarm() {
        checkpointTimer.alarmIn(config.getCheckpointIntervalInSeconds(), TimeUnit.SECONDS);
    }

    /**
     * Checkpoint with retries.
     *
     * @param checkpointer
     */
    private void checkpoint(IRecordProcessorCheckpointer checkpointer) {
        LOG.info("Checkpointing shard " + kinesisShardId);
        for (int i = 0; i < config.getCheckpointRetries(); i++) {
            try {
                // First checkpoint our persister to guarantee all calculated counts have been persisted
                persister.checkpoint();
                checkpointer.checkpoint();
                return;
            } catch (ShutdownException se) {
                // Ignore checkpoint if the processor instance has been shutdown (fail over).
                LOG.info("Caught shutdown exception, skipping checkpoint.", se);
                return;
            } catch (ThrottlingException e) {
                // Backoff and re-attempt checkpoint upon transient failures
                if (i >= (config.getCheckpointRetries() - 1)) {
                    LOG.error("Checkpoint failed after " + (i + 1) + "attempts.", e);
                    break;
                } else {
                    LOG.info("Transient issue when checkpointing - attempt " + (i + 1) + " of "
                            + config.getCheckpointRetries(),
                            e);
                }
            } catch (InvalidStateException e) {
                // This indicates an issue with the DynamoDB table (check for table, provisioned IOPS).
                LOG.error("Cannot save checkpoint to the DynamoDB table used by the Amazon Kinesis Client Library.", e);
                break;
            } catch (InterruptedException e) {
                LOG.error("Error encountered while checkpointing count persister.", e);
                // Fall through to attempt retry
            }
            try {
                Thread.sleep(config.getCheckpointBackoffTimeInSeconds());
            } catch (InterruptedException e) {
                LOG.debug("Interrupted sleep", e);
            }
        }
        // Handle this similar to a host or process crashing and abort the JVM.
        LOG.fatal("Couldn't successfully persistCounter data within max retry limit. Aborting the JVM to mimic a crash.");
        System.exit(1);
    }
}
