package com.amazonaws.services.kinesis.samples.datavis.kcl.persistence.ddb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by eugennekhai on 26/08/16.
 */
public class GeneralCountPersister<T> {
    private static final Log LOG = LogFactory.getLog(GeneralCountPersister.class);

    // Generate UTC timestamps
    protected static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    /**
     * This is used to limit the in memory queue. This number is the total counts we could generate for 10 unique
     * resources in 10 minutes if our update interval is 100ms.
     *
     * 10 resources * 10 minutes * 60 seconds * 10 intervals per second = 60,000.
     */
    private static final int MAX_COUNTS_IN_MEMORY = 60000;

    // The queue holds all pending obj counts to be sent to DynamoDB.
    public BlockingQueue<T> counts = new LinkedBlockingQueue<>(MAX_COUNTS_IN_MEMORY);

    public DynamoDBMapper mapper;

    // This thread is responsible for draining the queue of new counts and sending them in batches to DynamoDB
    public Thread dynamoDBSender;


    public GeneralCountPersister(DynamoDBMapper mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper must not be null");
        }

        this.mapper = mapper;
    }

    /**
     * Drain the queue of pending counts into the provided buffer and write those counts to DynamoDB. This blocks until
     * data is available in the queue.
     *
     * @param buffer A reusable buffer with sufficient space to drain the entire queue if necessary. This is provided as
     *        an optimization to avoid allocating a new buffer every interval.
     * @throws InterruptedException Thread interrupted while waiting for new data to arrive in the queue.
     */
    protected void sendQueueToDynamoDB(List<T> buffer) throws InterruptedException {
        // Block while waiting for data
        buffer.add(counts.take());
        // Drain as much of the queue as we can.
        // DynamoDBMapper will handle splitting the batch sizes for us.
        counts.drainTo(buffer);
        try {
            long start = System.nanoTime();
            // Write the contents of the buffer as items to our table
            List<DynamoDBMapper.FailedBatch> failures = mapper.batchWrite(buffer, Collections.emptyList());
            long end = System.nanoTime();
            LOG.info(String.format("%d new counts sent to DynamoDB in %dms",
                    buffer.size(),
                    TimeUnit.NANOSECONDS.toMillis(end - start)));

            for (DynamoDBMapper.FailedBatch failure : failures) {
                LOG.warn("Error sending count batch to DynamoDB. This will not be retried!", failure.getException());
            }
        } catch (Exception ex) {
            LOG.error("Error sending new counts to DynamoDB. The some counts may not be persisted.", ex);
        }
    }

    /**
     * Resolve the hostname of the machine executing this code.
     *
     * @return The hostname, or "unknown", if one cannot be determined.
     */
    public String resolveHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException uhe) {
            LOG.warn("Unable to determine hostname. Counts from this worker will be registered as counted by 'unknown'!",
                    uhe);
        }
        return "unknown";
    }

    public void checkpoint() throws InterruptedException {
        // We need to make sure all counts are flushed to DynamoDB before we return successfully.
        if (dynamoDBSender.isAlive()) {
            // If the DynamoDB thread is running wait until our counts queue is empty
            synchronized(counts) {
                while (!counts.isEmpty()) {
                    counts.wait();
                }
                // All the counts we currently know about have been persisted. It is now safe to return from this blocking call.
            }
        } else {
            throw new IllegalStateException("DynamoDB persister thread is not running. Counts are not persisted and we should not checkpoint!");
        }
    }
}
