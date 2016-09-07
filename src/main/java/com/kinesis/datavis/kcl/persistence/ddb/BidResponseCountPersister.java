package com.kinesis.datavis.kcl.persistence.ddb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.kinesis.datavis.kcl.persistence.CountPersister;
import com.kinesis.datavis.kcl.persistence.PersisterThread;
import com.kinesis.datavis.model.dynamo.BidResponseCount;
import com.kinesis.datavis.utils.HostResolver;
import com.kinesis.datavis.utils.Ticker;
import com.kinesis.openrtb.BidResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eugennekhai on 29/08/16.
 */
public class BidResponseCountPersister extends QueueRecordPersister implements CountPersister<BidResponse, BidResponseCount> {
    private static final Log LOG = LogFactory.getLog(BidRqCountPersister.class);

    public BidResponseCountPersister(DynamoDBMapper dbMapper) {
        super(dbMapper);
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

    @Override
    public void persistCounter(Map<BidResponse, Long> objectCounts) {
        if (objectCounts.isEmpty()) {
            // short circuit to avoid creating a map when we have no objects to persistCounter
            return;
        }

        Map<Date, BidResponseCount> countMap = new HashMap<>();

        for (Map.Entry<BidResponse, Long> count : objectCounts.entrySet()) {
            Date date = Calendar.getInstance(UTC).getTime();

            // Check for an existing counts for this resource
            BidResponse rec = count.getKey();
            BidResponseCount bdCount = countMap.get(date);

            if (bdCount == null) {
                // Create a new pair if this resource hasn't been seen yet in this batch
                bdCount = new BidResponseCount();
                bdCount.setHashKey(Ticker.getInstance().hashKey(rec.getAudienceId()));
                bdCount.setBannerId(rec.getBannerId());
                bdCount.setAudienceId(rec.getAudienceId());
                bdCount.setTimestamp(date);
                bdCount.setHost(HostResolver.resolveHostname());

                countMap.put(date, bdCount);
            }

            bdCount.setCount(bdCount.getCount() + count.getValue());
        }

        counts.addAll(countMap.values());
    }

    @Override
    public void persistCounters(Map<BidResponse, Long> objectCounts, Map<BidResponse, Double> objectSums) {

    }


}
