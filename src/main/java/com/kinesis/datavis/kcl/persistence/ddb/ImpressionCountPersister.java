package com.kinesis.datavis.kcl.persistence.ddb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.kinesis.datavis.kcl.persistence.CountPersister;
import com.kinesis.datavis.model.dynamo.ImpressionCount;
import com.kinesis.datavis.model.record.ImpressionRec;
import com.kinesis.datavis.utils.DynamoDBUtils;
import com.kinesis.datavis.utils.HostResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by eugennekhai on 29/08/16.
 */
public class ImpressionCountPersister extends QueueRecordPersister implements CountPersister<ImpressionRec, ImpressionCount> {
    private static final Log LOG = LogFactory.getLog(BidRqCountPersister.class);
    // Generate UTC timestamps
    protected static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public ImpressionCountPersister(DynamoDBMapper dbMapper) {
        super(dbMapper);
    }

    @Override
    public void persist(Map<ImpressionRec, Long> objectCounts) {
        if (objectCounts.isEmpty()) {
            // short circuit to avoid creating a map when we have no objects to persist
            return;
        }

        // Use a local collection to batch writing the new counts into the queue. This will allow the queue drainer
        // to remain simple as it doesn't have to account for less than full batches.

        // We map resource to pair counts so we can easily look up a resource and add referrer counts to it
        Map<Date, ImpressionCount> countMap = new HashMap<>();

        for (Map.Entry<ImpressionRec, Long> count : objectCounts.entrySet()) {
            Date date = Calendar.getInstance(UTC).getTime();

            // Check for an existing counts for this resource
            ImpressionRec rec = count.getKey();
            ImpressionCount bdCount = countMap.get(date);
            if (bdCount == null) {
                // Create a new pair if this resource hasn't been seen yet in this batch
                bdCount = new ImpressionCount();
                bdCount.setHashKey(DynamoDBUtils.getHashKey());
                bdCount.setTimestamp(date);
                bdCount.setBidRequestId(rec.getBidRequestId());
                bdCount.setTotalPrice(BigDecimal.ZERO);
                bdCount.setHost(HostResolver.resolveHostname());

                countMap.put(date, bdCount);
            }

            bdCount.setCount(bdCount.getCount() + count.getValue());
            bdCount.setTotalPrice(rec.getWinPrice());

        }

        counts.addAll(countMap.values());
    }

}

