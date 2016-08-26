package com.amazonaws.services.kinesis.samples.datavis.kcl.persistence.ddb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.kinesis.samples.datavis.kcl.persistence.CountPersister;
import com.amazonaws.services.kinesis.samples.datavis.kcl.persistence.PersisterThread;
import com.amazonaws.services.kinesis.samples.datavis.model.TypeCount;
import com.amazonaws.services.kinesis.samples.datavis.model.TypeCountComparator;
import com.amazonaws.services.kinesis.samples.datavis.model.dynamo.BidWinCount;
import com.amazonaws.services.kinesis.samples.datavis.model.record.BidWinRec;
import com.amazonaws.services.kinesis.samples.datavis.utils.DynamoDBUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Created by eugennekhai on 25/08/16.
 */
public class DynamoDBBidWinCountPersister extends GeneralCountPersister<BidWinCount> implements CountPersister<BidWinRec> {
    private static final Log LOG = LogFactory.getLog(DynamoDBBidRqCountPersister.class);

    // Generate UTC timestamps
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    /**
     * Create a new persister with a DynamoDBMapper to translate counts to items and send to Amazon DynamoDB.
     *
     * @param mapper Amazon DynamoDB Mapper to use.
     */
    public DynamoDBBidWinCountPersister(DynamoDBMapper mapper) {
        super(mapper);
    }

    @Override
    public void initialize() {
        // This thread is responsible for draining the queue of new counts and sending them in batches to DynamoDB
        dynamoDBSender = new PersisterThread<>(mapper, counts);

        dynamoDBSender.setDaemon(true);
        dynamoDBSender.start();
    }

    @Override
    public void persist(Map<BidWinRec, Long> objectCounts) {
        if (objectCounts.isEmpty()) {
            // short circuit to avoid creating a map when we have no objects to persist
            return;
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
                bdCount.setTypeCounts(new ArrayList<TypeCount>());
                bdCount.setHost(resolveHostname());

                countMap.put(date, bdCount);
            }

            bdCount.setCount(bdCount.getCount() + count.getValue());
            bdCount.setTotalPrice(bdCount.getTotalPrice().add(rec.getWinPrice()));

            // Add referrer to list of refcounts for this resource and time
            TypeCount typeCount = new TypeCount();
            typeCount.setType(rec.getType());
            typeCount.setCount(count.getValue());

            bdCount.getTypeCounts().add(typeCount);
        }

        // Top N calculation for this interval
        // By sorting the referrer counts list in descending order the consumer of the count data can choose their own
        // N.
        for (BidWinCount count : countMap.values()) {
            Collections.sort(count.getTypeCounts(), new TypeCountComparator());
        }

        counts.addAll(countMap.values());
    }

}
