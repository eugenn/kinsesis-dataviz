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

package com.amazonaws.services.kinesis.samples.datavis.writer;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.samples.datavis.producer.bidrq.BidRequestFactory;
import com.amazonaws.services.kinesis.samples.datavis.producer.bidrq.BidRequestPutter;
import com.amazonaws.services.kinesis.samples.datavis.utils.AppUtils;
import com.amazonaws.services.kinesis.samples.datavis.utils.StreamUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A command-line tool that sends records to Kinesis.
 */
public class BidRequestStreamWriter  {
    private static final Log LOG = LogFactory.getLog(BidRequestStreamWriter.class);


    /**
     * Start a number of threads and send randomly generated {@link }s to a Kinesis Stream until the
     * program is terminated.
     *
     * @param args Expecting 3 arguments: A numeric value indicating the number of threads to use to send
     *             data to Kinesis and the name of the stream to send records to, and the AWS region in which these resources
     *             exist or should be created.
     * @throws InterruptedException If this application is interrupted while sending records to Kinesis.
     */
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 3) {
            System.err.println("Usage: " + BidRequestStreamWriter.class.getSimpleName()
                    + " <number of threads> <stream name> <region>");
            System.exit(1);
        }

        int numberOfThreads = Integer.parseInt(args[0]);

        String streamName = args[1];

        Region region = AppUtils.parseRegion(args[2]);

        AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

        ClientConfiguration clientConfig = AppUtils.configureUserAgentForSample(new ClientConfiguration());

        AmazonKinesis kinesis = new AmazonKinesisClient(credentialsProvider, clientConfig);
        kinesis.setRegion(region);

        // The more resources we declare the higher write IOPS we need on our DynamoDB table.
        // We write a record for each resource every interval.
        // If interval = 500ms, resource count = 7 we need: (1000/500 * 7) = 14 write IOPS minimum.
        List<String> resources = new ArrayList<>();
        resources.add("300x200");
        resources.add("500x200");
        resources.add("400x600");
        resources.add("800x600");

        List<String> bidRequestIds = new ArrayList<>();
//        bidRequestIds.add(UUID.randomUUID().toString());
//        bidRequestIds.add(UUID.randomUUID().toString());
//        bidRequestIds.add(UUID.randomUUID().toString());
        bidRequestIds.add("11111111111");
//        bidRequestIds.add("22222222222");
//        bannerIds.add("33333333333");
        bidRequestIds.add("44444444444");

        BidRequestFactory bdFactory = new BidRequestFactory(bidRequestIds, resources, "bidrequest");

        // Creates a stream to write to with 2 shards if it doesn't exist
        StreamUtils streamUtils = new StreamUtils(kinesis);
        streamUtils.createStreamIfNotExists(streamName, 2);

        LOG.info(String.format("%s stream is ready for use", streamName));

        final BidRequestPutter putter = new BidRequestPutter(bdFactory, kinesis, streamName);

        GeneralStreamWriter streamWriter = new GeneralStreamWriter(numberOfThreads, putter);

        streamWriter.doWrite();

    }
}
