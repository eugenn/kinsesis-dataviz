package com.kinesis.datavis.kcl.persistence.ddb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.kinesis.datavis.kcl.persistence.CountPersister;
import com.kinesis.datavis.model.dynamo.ClicksCount;
import com.kinesis.datavis.model.record.ClicksRec;
import com.kinesis.datavis.utils.HostResolver;
import com.kinesis.datavis.utils.Ticker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eugennekhai on 29/08/16.
 */
public class ClicksCountPersister extends QueueRecordPersister implements CountPersister<ClicksRec, ClicksCount> {
    private static final Log LOG = LogFactory.getLog(BidRqCountPersister.class);

    public ClicksCountPersister(DynamoDBMapper dbMapper) {
        super(dbMapper);
    }

    @Override
    public void persistCounter(Map<ClicksRec, Long> objectCounts) {
        if (objectCounts.isEmpty()) {
            return;
        }

        Map<ClicksRec, ClicksCount> countMap = new HashMap<>();
        Calendar cal = Calendar.getInstance(UTC);

        for (Map.Entry<ClicksRec, Long> count : objectCounts.entrySet()) {
            ClicksRec rec = count.getKey();
            ClicksCount bdCount = countMap.get(rec);

            if (bdCount == null) {
                bdCount = new ClicksCount();
                bdCount.setHashKey(Ticker.getInstance().hashKey(rec.getAudienceId()));

                bdCount.setBannerId(rec.getBannerId());
                bdCount.setAudienceId(rec.getAudienceId());
                bdCount.setBidRequestId(rec.getBidRequestId());
                bdCount.setHost(HostResolver.resolveHostname());

                countMap.put(rec, bdCount);
            }

            bdCount.setTimestamp(cal.getTime());
            bdCount.setCount(bdCount.getCount() + count.getValue());

        }

        counts.addAll(countMap.values());
    }

    @Override
    public void persistCounters(Map<ClicksRec, Long> objectCounts, Map<ClicksRec, Double> objectSums) {

    }


}
