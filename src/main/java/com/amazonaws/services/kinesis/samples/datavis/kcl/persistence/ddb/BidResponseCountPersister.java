package com.amazonaws.services.kinesis.samples.datavis.kcl.persistence.ddb;

import com.amazonaws.services.kinesis.samples.datavis.kcl.persistence.CountPersister;
import com.amazonaws.services.kinesis.samples.datavis.model.dynamo.BidResponseCount;
import com.amazonaws.services.kinesis.samples.datavis.model.record.BidResponseRec;
import com.amazonaws.services.kinesis.samples.datavis.utils.DynamoDBUtils;
import com.amazonaws.services.kinesis.samples.datavis.utils.HostResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Created by eugennekhai on 29/08/16.
 */
public class BidResponseCountPersister implements CountPersister<BidResponseRec, BidResponseCount> {
    private static final Log LOG = LogFactory.getLog(BidRqCountPersister.class);
    // Generate UTC timestamps
    protected static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    @Override
    public Collection<BidResponseCount> persist(Map<BidResponseRec, Long> objectCounts) {
        if (objectCounts.isEmpty()) {
            // short circuit to avoid creating a map when we have no objects to persist
            return new ArrayList<>();
        }

        // Use a local collection to batch writing the new counts into the queue. This will allow the queue drainer
        // to remain simple as it doesn't have to account for less than full batches.

        // We map resource to pair counts so we can easily look up a resource and add referrer counts to it
        Map<Date, BidResponseCount> countMap = new HashMap<>();

        for (Map.Entry<BidResponseRec, Long> count : objectCounts.entrySet()) {
            Date date = Calendar.getInstance(UTC).getTime();

            // Check for an existing counts for this resource
            BidResponseRec rec = count.getKey();
            BidResponseCount bdCount = countMap.get(date);
            if (bdCount == null) {
                // Create a new pair if this resource hasn't been seen yet in this batch
                bdCount = new BidResponseCount();
                bdCount.setHashKey(DynamoDBUtils.getHashKey());
                bdCount.setBannerId(rec.getBannerId());
                bdCount.setTimestamp(date);
//                bdCount.setTypeCounts(new ArrayList<TypeCount>());
                bdCount.setHost(HostResolver.resolveHostname());

                countMap.put(date, bdCount);
            }

            bdCount.setCount(bdCount.getCount() + count.getValue());

            // Add referrer to list of refcounts for this resource and time
//            TypeCount typeCount = new TypeCount();
//            typeCount.setType(rec.getType());
//            typeCount.setCount(count.getValue());
//
//            bdCount.getTypeCounts().add(typeCount);
        }

        // Top N calculation for this interval
        // By sorting the referrer counts list in descending order the consumer of the count data can choose their own
        // N.
//        for (BidResponseCount count : countMap.values()) {
//            Collections.sort(count.getTypeCounts(), new TypeCountComparator());
//        }

        return countMap.values();
    }



}
