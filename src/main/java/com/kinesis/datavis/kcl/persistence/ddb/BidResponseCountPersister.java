package com.kinesis.datavis.kcl.persistence.ddb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.kinesis.datavis.kcl.persistence.CountPersister;
import com.kinesis.datavis.kcl.persistence.MapperThread;
import com.kinesis.datavis.kcl.persistence.PersisterThread;
import com.kinesis.datavis.model.dynamo.BannerRequestMapper;
import com.kinesis.datavis.model.dynamo.BidResponseCount;
import com.kinesis.datavis.utils.DynamoDBUtils;
import com.kinesis.datavis.utils.HostResolver;
import com.kinesis.openrtb.BidResponse;
import lombok.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by eugennekhai on 29/08/16.
 */
public class BidResponseCountPersister extends QueueRecordPersister implements CountPersister<BidResponse, BidResponseCount> {
    private static final Log LOG = LogFactory.getLog(BidRqCountPersister.class);
    // Generate UTC timestamps
    protected static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    @Getter
    public BlockingQueue<BannerRequestMapper> counts2 = new LinkedBlockingQueue<>(60000);

    MapperThread<BannerRequestMapper> mapperThread;
    DynamoDBMapper dbMapper2;

    public BidResponseCountPersister(DynamoDBMapper dbMapper, DynamoDBMapper dbMapper2) {
        super(dbMapper);
        this.dbMapper2 = dbMapper2;
    }

    public void initialize() {
        // This thread is responsible for draining the queue of new counts and sending them in batches to DynamoDB
        dynamoDBSender = new PersisterThread<>(mapper, counts);
        dynamoDBSender.setDaemon(true);
        dynamoDBSender.start();

//        mapperThread = new MapperThread<>(dbMapper2, counts2);
//        mapperThread.setDaemon(true);
//        mapperThread.start();
    }

    public void checkpoint() throws InterruptedException {
        // We need to make sure all counts are flushed to DynamoDB before we return successfully.
        if (dynamoDBSender.isAlive()) {
            // If the DynamoDB thread is running wait until our counts queue is empty
            synchronized(counts) {
                while (!counts.isEmpty()) {
                    counts.wait();
//                    counts2.wait();
                }
                // All the counts we currently know about have been persisted. It is now safe to return from this blocking call.
            }
//            synchronized(counts2) {
//                while (!counts2.isEmpty()) {
//                    counts2.wait();
//                }
//                // All the counts we currently know about have been persisted. It is now safe to return from this blocking call.
//            }
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

        List<BannerRequestMapper> bnrqs = new ArrayList<>();

        Map<Date, BidResponseCount> countMap = new HashMap<>();

        for (Map.Entry<BidResponse, Long> count : objectCounts.entrySet()) {
            Date date = Calendar.getInstance(UTC).getTime();

            // Check for an existing counts for this resource
            BidResponse rec = count.getKey();
            BidResponseCount bdCount = countMap.get(date);
            String hashKey = DynamoDBUtils.getHashKey();

            bnrqs.add(new BannerRequestMapper(hashKey, date, rec.getBannerId(), rec.getId(), rec.getAudienceId()));

            if (bdCount == null) {
                // Create a new pair if this resource hasn't been seen yet in this batch
                bdCount = new BidResponseCount();
                bdCount.setHashKey(hashKey);
                bdCount.setBannerId(rec.getId());
                bdCount.setTimestamp(date);
                bdCount.setHost(HostResolver.resolveHostname());

                countMap.put(date, bdCount);
            }

            bdCount.setCount(bdCount.getCount() + count.getValue());
        }



        counts.addAll(countMap.values());
//        counts2.addAll(bnrqs);



    }

    @Override
    public void persistCounters(Map<BidResponse, Long> objectCounts, Map<BidResponse, Double> objectSums) {

    }


}
