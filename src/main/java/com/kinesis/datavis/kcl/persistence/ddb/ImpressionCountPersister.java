package com.kinesis.datavis.kcl.persistence.ddb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.kinesis.datavis.kcl.persistence.CountPersister;
import com.kinesis.datavis.model.dynamo.ImpressionCount;
import com.kinesis.datavis.model.record.ImpressionRec;
import com.kinesis.datavis.utils.HostResolver;
import com.kinesis.datavis.utils.Ticker;

import java.util.*;

/**
 * Created by eugennekhai on 29/08/16.
 */
public class ImpressionCountPersister extends QueueRecordPersister implements CountPersister<ImpressionRec, ImpressionCount> {

    public ImpressionCountPersister(DynamoDBMapper dbMapper) {
        super(dbMapper);
    }


    @Override
    public void persistCounter(Map<ImpressionRec, Long> objectCounts) {

    }

    @Override
    public void persistCounters(Map<ImpressionRec, Long> objectCounts, Map<ImpressionRec, Double> objectSums) {
        if (objectCounts.isEmpty()) {
            return;
        }

        Map<ImpressionRec, ImpressionCount> countMap = new HashMap<>();

        Iterator<Map.Entry<ImpressionRec, Double>> iter = objectSums.entrySet().iterator();
        Calendar cal = Calendar.getInstance(UTC);

        int i = 1;
        for (Map.Entry<ImpressionRec, Long> count : objectCounts.entrySet()) {

            ImpressionRec rec = count.getKey();
            Map.Entry<ImpressionRec, Double> totalPrice = iter.next();

            ImpressionCount bdCount = countMap.get(rec);
            if (bdCount == null) {
                bdCount = new ImpressionCount();

                bdCount.setHashKey(Ticker.getInstance().hashKey(rec.getAudienceId()));

                bdCount.setBidRequestId(rec.getBidRequestId());
                bdCount.setBannerId(rec.getBannerId());
                bdCount.setAudienceId(rec.getAudienceId());
                bdCount.setHost(HostResolver.resolveHostname());

                countMap.put(rec, bdCount);
            }

            bdCount.setTimestamp(new Date(cal.getTimeInMillis() + i++));
            bdCount.setCount(bdCount.getCount() + count.getValue());
            bdCount.setTotalPrice(totalPrice.getValue());

        }

        counts.addAll(countMap.values());
    }
}

