package com.kinesis.datavis.kcl.persistence.ddb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.kinesis.datavis.kcl.persistence.PersisterThread;
import lombok.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by eugennekhai on 26/08/16.
 */
public class QueueRecordPersister<C> {
    private static final Log LOG = LogFactory.getLog(QueueRecordPersister.class);

    // Generate UTC timestamps
    protected static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    // The queue holds all pending obj counts to be sent to DynamoDB.
    @Getter
    public BlockingQueue<C> counts = new LinkedBlockingQueue<>(PersisterThread.MAX_COUNTS_IN_MEMORY);


    public DynamoDBMapper mapper;

    // This thread is responsible for draining the queue of new counts and sending them in batches to DynamoDB
    public Thread dynamoDBSender;


    public QueueRecordPersister(DynamoDBMapper mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper must not be null");
        }

        this.mapper = mapper;
    }

    public void initialize() {
        // This thread is responsible for draining the queue of new counts and sending them in batches to DynamoDB
        dynamoDBSender = new PersisterThread<>(mapper, counts);

        dynamoDBSender.setDaemon(true);
        dynamoDBSender.start();
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
