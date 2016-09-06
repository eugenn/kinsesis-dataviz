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
            return;
        }

        Map<BidWinRec, BidWinCount> countMap = new HashMap<>();

        Iterator<Map.Entry<BidWinRec, Double>> iter = objectSums.entrySet().iterator();
        Calendar cal = Calendar.getInstance(UTC);

        for (Map.Entry<BidWinRec, Long> count : objectCounts.entrySet()) {
            BidWinRec rec = count.getKey();
            Map.Entry<BidWinRec, Double> totalPrice = iter.next();

            BidWinCount bdCount = countMap.get(rec);
            if (bdCount == null) {
                bdCount = new BidWinCount();

                bdCount.setHashKey(Ticker.getInstance().hashKey(rec.getAudienceId()));

                bdCount.setBannerId(rec.getBannerId());
                bdCount.setAudienceId(rec.getAudienceId());

                bdCount.setHost(HostResolver.resolveHostname());
                countMap.put(rec, bdCount);
            }

            bdCount.setTimestamp(cal.getTime());
            bdCount.setTotalPrice(bdCount.getTotalPrice() + totalPrice.getValue());
            bdCount.setCount(bdCount.getCount() + count.getValue());

        }

        counts.addAll(countMap.values());
    }

}
