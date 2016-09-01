package com.kinesis.datavis.kcl.persistence;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by eugennekhai on 31/08/16.
 */
public class MapperThread<T> extends Thread {
    private static final Log LOG = LogFactory.getLog(PersisterThread.class);
    private static final int MAX_COUNTS_IN_MEMORY = 60000;
    private DynamoDBMapper mapper;
    private final BlockingQueue<T> counts;

    public MapperThread(DynamoDBMapper mapper, BlockingQueue<T> counts) {
        this.mapper = mapper;
        this.counts = counts;
    }

    @Override
    public void run() {
        // Create a reusable buffer to drain our queue into.
        List<T> buffer = new ArrayList<>(MAX_COUNTS_IN_MEMORY);

        // Continuously attempt to drain the queue and send counts to DynamoDB until this thread is interrupted
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Drain anything that's in the queue to the buffer and write the items to DynamoDB
                sendQueueToDynamoDB(buffer);

                synchronized(counts) {
                    if (counts.isEmpty()) {
                        counts.notify();
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // Clear the temporary buffer to release references to persisted counts
                buffer.clear();
            }
        }
    }

    /**
     * Drain the queue of pending counts into the provided buffer and write those counts to DynamoDB. This blocks until
     * data is available in the queue.
     *
     * @param buffer A reusable buffer with sufficient space to drain the entire queue if necessary. This is provided as
     *        an optimization to avoid allocating a new buffer every interval.
     * @throws InterruptedException Thread interrupted while waiting for new data to arrive in the queue.
     */
    public void sendQueueToDynamoDB(List<T> buffer) throws InterruptedException {
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
}
