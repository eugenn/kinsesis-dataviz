package com.kinesis.datavis.kcl.persistence.ddb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.kinesis.datavis.kcl.persistence.CountPersister;
import com.kinesis.datavis.model.dynamo.BidResponseCount;
import com.kinesis.datavis.utils.HostResolver;
import com.kinesis.datavis.utils.Ticker;
import com.kinesis.openrtb.BidResponse;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eugennekhai on 29/08/16.
 */
public class BidResponseCountPersister extends QueueRecordPersister implements CountPersister<BidResponse, BidResponseCount> {

    public BidResponseCountPersister(DynamoDBMapper dbMapper) {
        super(dbMapper);
    }


    @Override
    public void persistCounter(Map<BidResponse, Long> objectCounts) {
        if (objectCounts.isEmpty()) {
            // short circuit to avoid creating a map when we have no objects to persistCounter
            return;
        }

        Map<BidResponse, BidResponseCount> countMap = new HashMap<>();
        Calendar cal = Calendar.getInstance(UTC);

        int i = 1;

        for (Map.Entry<BidResponse, Long> count : objectCounts.entrySet()) {
            BidResponse rec = count.getKey();
            BidResponseCount bdCount = countMap.get(rec);

            if (bdCount == null) {
                bdCount = new BidResponseCount();
                bdCount.setHashKey(Ticker.getInstance().hashKey(rec.getAudienceId()));

                bdCount.setBannerId(rec.getBannerId());
                bdCount.setAudienceId(rec.getAudienceId());

                bdCount.setHost(HostResolver.resolveHostname());

                countMap.put(rec, bdCount);
            }

            bdCount.setTimestamp(new Date(cal.getTimeInMillis() + i++));
            bdCount.setCount(bdCount.getCount() + count.getValue());
        }

        counts.addAll(countMap.values());
    }

    @Override
    public void persistCounters(Map<BidResponse, Long> objectCounts, Map<BidResponse, Double> objectSums) {

    }


}
