package com.kinesis.datavis.kcl.persistence.ddb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.kinesis.datavis.kcl.persistence.CountPersister;
import com.kinesis.datavis.model.dynamo.BidWinCount;
import com.kinesis.datavis.model.record.BidWinRec;
import com.kinesis.datavis.utils.HostResolver;
import com.kinesis.datavis.utils.Ticker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Created by eugennekhai on 25/08/16.
 */
public class BidWinCountPersister extends QueueRecordPersister implements CountPersister<BidWinRec, BidWinCount> {
    private static final Log LOG = LogFactory.getLog(BidRqCountPersister.class);

    // Generate UTC timestamps
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public BidWinCountPersister(DynamoDBMapper dbMapper) {
        super(dbMapper);
    }

    @Override
    public void persistCounter(Map<BidWinRec, Long> objectCounts) {

    }

    @Override
    public void persistCounters(Map<BidWinRec, Long> objectCounts, Map<BidWinRec, Double> objectSums) {
        if (objectCounts.isEmpty()) {
            // short circuit to avoid creating a map when we have no objects to persistCounter
            return;
        }

        // Use a local collection to batch writing the new counts into the queue. This will allow the queue drainer
        // to remain simple as it doesn't have to account for less than full batches.

        // We map resource to pair counts so we can easily look up a resource and add referrer counts to it
        Map<Date, BidWinCount> countMap = new HashMap<>();

        Iterator<Map.Entry<BidWinRec, Double>> iter = objectSums.entrySet().iterator();

        for (Map.Entry<BidWinRec, Long> count : objectCounts.entrySet()) {
            Date date = Calendar.getInstance(UTC).getTime();
            // Check for an existing counts for this resource
            BidWinRec rec = count.getKey();
            Map.Entry<BidWinRec, Double> totalPrice = iter.next();

            BidWinCount bdCount = countMap.get(date);
            if (bdCount == null) {
                // Create a new pair if this resource hasn't been seen yet in this batch
                bdCount = new BidWinCount();
                bdCount.setHashKey(Ticker.getInstance().hashKey());

                bdCount.setBannerId(rec.getBannerId());
                bdCount.setAudienceId(rec.getAudienceId());

                bdCount.setTimestamp(date);
                bdCount.setHost(HostResolver.resolveHostname());

                countMap.put(date, bdCount);
            }

            bdCount.setTotalPrice(totalPrice.getValue());
            bdCount.setCount(bdCount.getCount() + count.getValue());

        }

        counts.addAll(countMap.values());
    }

}
