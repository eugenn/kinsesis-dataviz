package com.kinesis.datavis.app;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.kinesis.datavis.utils.DynamoDBUtils;
import com.kinesis.datavis.utils.AppUtils;
import com.kinesis.datavis.utils.StreamUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.UUID;

/**
 * Created by eugennekhai on 26/08/16.
 */
public class CounterApp {
    private static final Log LOG = LogFactory.getLog(BidRequestCounter.class);

    // Count occurrences of HTTP referrer pairs over a range of 10 seconds
    private static final int COMPUTE_RANGE_FOR_COUNTS_IN_MILLIS = 1000;
    // Update the counts every 1 second
    private static final int COMPUTE_INTERVAL_IN_MILLIS = 1000;

    public static DynamoDBMapper createMapper(String applicationName, String streamName, String countsTableName, Region region) {
        AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

        ClientConfiguration clientConfig = AppUtils.configureUserAgentForSample(new ClientConfiguration());

        AmazonKinesis kinesis = new AmazonKinesisClient(credentialsProvider, clientConfig);
        kinesis.setRegion(region);

        AmazonDynamoDB dynamoDB = new AmazonDynamoDBClient(credentialsProvider, clientConfig);
        dynamoDB.setRegion(region);

        // Creates a stream to write to, if it doesn't exist
        StreamUtils streamUtils = new StreamUtils(kinesis);
        streamUtils.createStreamIfNotExists(streamName, 2);
        LOG.info(String.format("%s stream is ready for use", streamName));

        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDB);
        dynamoDBUtils.createCountTableIfNotExists(countsTableName);
        LOG.info(String.format("%s DynamoDB table is ready for use", countsTableName));

        return dynamoDBUtils.createMapperForTable(countsTableName);
    }

    public static DynamoDBMapper createMapperForMappingTable(Region region) {
        AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

        ClientConfiguration clientConfig = AppUtils.configureUserAgentForSample(new ClientConfiguration());

        AmazonDynamoDB dynamoDB = new AmazonDynamoDBClient(credentialsProvider, clientConfig);
        dynamoDB.setRegion(region);


        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDB);
        dynamoDBUtils.createCountTableIfNotExists("RequestBannerAudience", "bidRequestId", "timestamp");


        return dynamoDBUtils.createMapperForTable("RequestBannerAudience");
    }

    public static KinesisClientLibConfiguration buildConfig(String applicationName, String streamName, Region region) {
        String workerId = String.valueOf(UUID.randomUUID());
        LOG.info(String.format("Using working id: %s", workerId));


        KinesisClientLibConfiguration kclConfig =
                new KinesisClientLibConfiguration(applicationName, streamName, new DefaultAWSCredentialsProviderChain(), workerId);
        kclConfig.withCommonClientConfig(AppUtils.configureUserAgentForSample(new ClientConfiguration()));
        kclConfig.withRegionName(region.getName());
        kclConfig.withInitialPositionInStream(InitialPositionInStream.LATEST);

        return kclConfig;
    }

    public static int runWorker(IRecordProcessorFactory recordProcessor, KinesisClientLibConfiguration kclConfig) {
        Worker worker = new Worker(recordProcessor, kclConfig);

        int exitCode = 0;
        try {
            worker.run();
        } catch (Throwable t) {
            LOG.error("Caught throwable while processing data.", t);
            exitCode = 1;
        }

        return exitCode;
    }
}

