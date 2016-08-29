package com.amazonaws.services.kinesis.samples.datavis.kcl.persistence.ddb;

import com.amazonaws.services.kinesis.samples.datavis.kcl.persistence.CountPersister;
import com.amazonaws.services.kinesis.samples.datavis.model.dynamo.BidWinCount;
import com.amazonaws.services.kinesis.samples.datavis.model.record.BidWinRec;
import com.amazonaws.services.kinesis.samples.datavis.utils.DynamoDBUtils;
import com.amazonaws.services.kinesis.samples.datavis.utils.HostResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Created by eugennekhai on 25/08/16.
 */
public class BidWinCountPersister implements CountPersister<BidWinRec, BidWinCount> {
    private static final Log LOG = LogFactory.getLog(BidRqCountPersister.class);

    // Generate UTC timestamps
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");


    @Override
    public Collection<BidWinCount> persist(Map<BidWinRec, Long> objectCounts) {
        if (objectCounts.isEmpty()) {
            // short circuit to avoid creating a map when we have no objects to persist
            return new ArrayList<>();
        }

        // Use a local collection to batch writing the new counts into the queue. This will allow the queue drainer
        // to remain simple as it doesn't have to account for less than full batches.

        // We map resource to pair counts so we can easily look up a resource and add referrer counts to it
        Map<Date, BidWinCount> countMap = new HashMap<>();

        for (Map.Entry<BidWinRec, Long> count : objectCounts.entrySet()) {
            Date date = Calendar.getInstance(UTC).getTime();
            // Check for an existing counts for this resource
            BidWinRec rec = count.getKey();
            BidWinCount bdCount = countMap.get(date);
            if (bdCount == null) {
                // Create a new pair if this resource hasn't been seen yet in this batch
                bdCount = new BidWinCount();
                bdCount.setHashKey(DynamoDBUtils.getHashKey());
                bdCount.setBidRequestId(rec.getBidRequestId());
                bdCount.setTimestamp(date);
                bdCount.setHost(HostResolver.resolveHostname());

                countMap.put(date, bdCount);
            }

            bdCount.setCount(bdCount.getCount() + count.getValue());
            bdCount.setTotalPrice(bdCount.getTotalPrice().add(rec.getWinPrice()));


        }

        return countMap.values();
    }

}
