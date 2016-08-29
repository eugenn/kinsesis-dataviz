/*
 * Copyright 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazonaws.services.kinesis.samples.datavis.kcl.persistence.ddb;

import com.amazonaws.services.kinesis.samples.datavis.kcl.persistence.CountPersister;
import com.amazonaws.services.kinesis.samples.datavis.model.dynamo.BidRequestCount;
import com.amazonaws.services.kinesis.samples.datavis.model.record.BidRequestRec;
import com.amazonaws.services.kinesis.samples.datavis.utils.DynamoDBUtils;
import com.amazonaws.services.kinesis.samples.datavis.utils.HostResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Persists counts to DynamoDB. This uses a separate thread to send counts to DynamoDB to decouple any network latency
 * from affecting the thread we use to update counts.
 */
public class BidRqCountPersister implements CountPersister<BidRequestRec, BidRequestCount> {
    private static final Log LOG = LogFactory.getLog(BidRqCountPersister.class);
    // Generate UTC timestamps
    protected static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    @Override
    public Collection<BidRequestCount> persist(Map<BidRequestRec, Long> objectCounts) {
        if (objectCounts.isEmpty()) {
            // short circuit to avoid creating a map when we have no objects to persist
            return new ArrayList<>();
        }

        // Use a local collection to batch writing the new counts into the queue. This will allow the queue drainer
        // to remain simple as it doesn't have to account for less than full batches.

        // We map resource to pair counts so we can easily look up a resource and add referrer counts to it
        Map<Date, BidRequestCount> countMap = new HashMap<>();

        for (Map.Entry<BidRequestRec, Long> count : objectCounts.entrySet()) {
            Date date = Calendar.getInstance(UTC).getTime();

            // Check for an existing counts for this resource
            BidRequestRec rec = count.getKey();
            BidRequestCount bdCount = countMap.get(date);
            if (bdCount == null) {
                // Create a new pair if this resource hasn't been seen yet in this batch
                bdCount = new BidRequestCount();
                bdCount.setHashKey(DynamoDBUtils.getHashKey());
                bdCount.setWh(rec.getWh());
                bdCount.setTimestamp(date);
                bdCount.setHost(HostResolver.resolveHostname());

                countMap.put(date, bdCount);
            }

            bdCount.setCount(bdCount.getCount() + count.getValue());
        }

        return countMap.values();
    }



}
