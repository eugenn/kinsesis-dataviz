package com.kinesis.datavis.kcl.persistence;

import com.jdbc.dao.MappingDAO;
import com.jdbc.vo.Mapping;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by eugennekhai on 31/08/16.
 */
public class MappingThread<T> extends Thread {
    private static final Log LOG = LogFactory.getLog(PersisterThread.class);
    private final BlockingQueue<T> eventsQueue;
    private MappingDAO mappingDAO;

    public MappingThread(MappingDAO mappingDAO, BlockingQueue<T> eventsQueue) {
        this.mappingDAO = mappingDAO;
        this.eventsQueue = eventsQueue;
    }

    @Override
    public void run() {
        // Create a reusable buffer to drain our queue into.
        List<T> buffer = new ArrayList<>(PersisterThread.MAX_COUNTS_IN_MEMORY * 2);

        // Continuously attempt to drain the queue and send eventsQueue to DynamoDB until this thread is interrupted
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Drain anything that's in the queue to the buffer and write the items to DynamoDB
                sendQueueToDynamoDB(buffer);

                synchronized(eventsQueue) {
                    if (eventsQueue.isEmpty()) {
                        eventsQueue.notify();
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // Clear the temporary buffer to release references to persisted eventsQueue
                buffer.clear();
            }
        }
    }

    /**
     * Drain the queue of pending eventsQueue into the provided buffer and write those eventsQueue to DynamoDB. This blocks until
     * data is available in the queue.
     *
     * @param buffer A reusable buffer with sufficient space to drain the entire queue if necessary. This is provided as
     *        an optimization to avoid allocating a new buffer every interval.
     * @throws InterruptedException Thread interrupted while waiting for new data to arrive in the queue.
     */
    public void sendQueueToDynamoDB(List<T> buffer) throws InterruptedException {
        // Block while waiting for data
        buffer.add(eventsQueue.take());
        // Drain as much of the queue as we can.
        eventsQueue.drainTo(buffer);

        try {
            long start = System.nanoTime();
            // Write the contents of the buffer as items to our table

            mappingDAO.batchInsert((List<Mapping>) buffer);

            long end = System.nanoTime();
            LOG.info(String.format("%d new eventsQueue sent to DynamoDB in %dms",
                    buffer.size(),
                    TimeUnit.NANOSECONDS.toMillis(end - start)));


        } catch (Exception ex) {
            LOG.error("Error sending new eventsQueue to DynamoDB. The some eventsQueue may not be persisted.", ex);
        }
    }
}
