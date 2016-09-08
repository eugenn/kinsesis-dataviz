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

package com.kinesis.datavis.app;

import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.jdbc.dao.JDBCMappingDAO;
import com.kinesis.connectors.s3.buffer.FlushBuffer;
import com.kinesis.connectors.s3.emitter.S3Emitter;
import com.kinesis.datavis.kcl.persistence.CountPersister;
import com.kinesis.datavis.kcl.persistence.ddb.BidRqCountPersister;
import com.kinesis.datavis.kcl.processor.CountingRecordProcessorFactory;
import com.kinesis.datavis.kcl.processor.type.BidRqProcessor;
import com.kinesis.datavis.kcl.processor.type.TypeProcessor;
import com.kinesis.datavis.utils.AppProperties;
import com.kinesis.datavis.utils.AppUtils;
import com.kinesis.openrtb.BidRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.UnknownHostException;

/**
 * Amazon Kinesis application to count distinct {@link }s over a sliding window. Counts are persisted
 * every update interval by a {@link CountPersister}.
 */
public class BidRequestCounter extends CounterApp {
    private static final Log LOG = LogFactory.getLog(BidRequestCounter.class);

    // Count occurrences over a range of 10 seconds
    private static final int COMPUTE_RANGE_FOR_COUNTS_IN_MILLIS = 1000;
    // Update the counts every 1 second
    private static final int COMPUTE_INTERVAL_IN_MILLIS = 1000;

    /**
     * Start the Kinesis Client application.
     * 
     * @param args Expecting 4 arguments: Application name to use for the Kinesis Client Application, Stream name to
     *        read from, DynamoDB table name to persistCounter counts into, and the AWS region in which these resources
     *        exist or should be created.
     */
    public static void main(String[] args) throws UnknownHostException {
        String path = args[0];

        AppProperties appProps = new AppProperties("bidrq", path);

        String applicationName = appProps.appName();
        String streamName = appProps.streamName();
        String countsTableName = appProps.countTable();
        Region region = AppUtils.parseRegion(appProps.getRegion());

        DynamoDBMapper mapper = createMapper(streamName, countsTableName, region);

        TypeProcessor<BidRequest> typeProcessor =
                new BidRqProcessor(new JDBCMappingDAO(appProps.dbUrl(), appProps.dbUser(), appProps.dbPassword()), new FlushBuffer<>());

        IRecordProcessorFactory recordProcessor =
                new CountingRecordProcessorFactory<>(BidRequest.class,
                        new BidRqCountPersister(mapper),
                        typeProcessor,
                        new S3Emitter("bdrequest"),
                        COMPUTE_RANGE_FOR_COUNTS_IN_MILLIS,
                        COMPUTE_INTERVAL_IN_MILLIS);

        KinesisClientLibConfiguration kclConfig = buildConfig(applicationName, streamName, region);

        int exitCode = runWorker(recordProcessor, kclConfig);

        System.exit(exitCode);
    }
}
