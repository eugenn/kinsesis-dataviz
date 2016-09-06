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

package com.kinesis.datavis.kcl.persistence.ddb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.kinesis.datavis.kcl.persistence.CountPersister;
import com.kinesis.datavis.model.dynamo.BidRequestCount;
import com.kinesis.datavis.utils.HostResolver;
import com.kinesis.datavis.utils.Ticker;
import com.kinesis.openrtb.BidRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Persists counts to DynamoDB. This uses a separate thread to send counts to DynamoDB to decouple any network latency
 * from affecting the thread we use to update counts.
 */
public class BidRqCountPersister extends QueueRecordPersister implements CountPersister<BidRequest, BidRequestCount> {
    private static final Log LOG = LogFactory.getLog(BidRqCountPersister.class);

    public BidRqCountPersister(DynamoDBMapper dbMapper) {
        super(dbMapper);
    }

    @Override
    public void persistCounter(Map<BidRequest, Long> objectCounts) {
        if (objectCounts.isEmpty()) {
            return;
        }

        Map<Date, BidRequestCount> countMap = new HashMap<>();

        for (Map.Entry<BidRequest, Long> count : objectCounts.entrySet()) {
            Date date = Calendar.getInstance(UTC).getTime();

            BidRequest rec = count.getKey();
            BidRequestCount bdCount = countMap.get(date);
            if (bdCount == null) {
                bdCount = new BidRequestCount();
                bdCount.setHashKey(Ticker.getInstance().hashKey());
//                bdCount.setWh(rec.getDevice().getWidth() + "x" + rec.getDevice().getHeight());
//                bdCount.setBidRequestId(rec.getRequestId());
                bdCount.setTimestamp(date);
                bdCount.setHost(HostResolver.resolveHostname());

                countMap.put(date, bdCount);
            }

            bdCount.setCount(bdCount.getCount() + count.getValue());
        }

        counts.addAll(countMap.values());

    }

    @Override
    public void persistCounters(Map<BidRequest, Long> objectCounts, Map<BidRequest, Double> objectSums) {

    }


}
