package com.kinesis.datavis.writer;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.kinesis.datavis.producer.impression.ImpressionFactory;
import com.kinesis.datavis.producer.impression.ImpressionPutter;
import com.kinesis.datavis.utils.AppProperties;
import com.kinesis.datavis.utils.AppUtils;
import com.kinesis.datavis.utils.StreamUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eugennekhai on 29/08/16.
 */
public class ImpressionStreamWriter {
    private static final Log LOG = LogFactory.getLog(ImpressionStreamWriter.class);

    /**
     * The amount of time to wait between records.
     * <p>
     * We want to send at most 10 records per second per thread so we'll delay 100ms between records.
     * This keeps the overall cost low for this sample.
     */
    private static final long DELAY_BETWEEN_RECORDS_IN_MILLIS = 100;

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
        int numberOfThreads = Integer.parseInt(args[0]);

        AppProperties appProps = new AppProperties("impression", args[1]);

        String streamName = appProps.streamName();

        Region region = AppUtils.parseRegion(appProps.getRegion());

        AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

        ClientConfiguration clientConfig = AppUtils.configureUserAgentForSample(new ClientConfiguration());

        AmazonKinesis kinesis = new AmazonKinesisClient(credentialsProvider, clientConfig);
        kinesis.setRegion(region);

        List<String> bidRequestIds = new ArrayList<>();
//        bidRequestIds.add(UUID.randomUUID().toString());
//        bidRequestIds.add(UUID.randomUUID().toString());
//        bidRequestIds.add("b05361fe-4413-4a0e-92ae-cc2d9314ba16"); //f
//        bidRequestIds.add("3523784b-38f7-4afc-985a-62736dbdcbf1"); //f
//        bidRequestIds.add("e268b3fa-d53b-4f73-bb2f-31f429831e4a"); //m
//        bidRequestIds.add("fcc5f84b-c004-43c8-b25b-a2548614dff9"); //m
        bidRequestIds.add("929f132d-b3cc-4608-a893-64aeb9c8cfe9"); //m
        bidRequestIds.add("0b502371-3fd6-44d7-ae35-5716dde6f3a8"); //m

        List<Double> prices = new ArrayList<>();
        prices.add(Double.valueOf(1.00));
        prices.add(Double.valueOf(1.00));
//        prices.add(Double.valueOf(1.00));

//        prices.add(Double.valueOf(2.83));
//        prices.add(Double.valueOf(1.03));
//        prices.add(Double.valueOf(0.53));
//        prices.add(Double.valueOf(2.33));
//        prices.add(Double.valueOf(0.223));
//        prices.add(Double.valueOf(2.11));
//        prices.add(Double.valueOf(3.33));
//        prices.add(Double.valueOf(1.673));

        ImpressionFactory impFactory = new ImpressionFactory(bidRequestIds, prices);

        // Creates a stream to write to with 2 shards if it doesn't exist
        StreamUtils streamUtils = new StreamUtils(kinesis);
        streamUtils.createStreamIfNotExists(streamName, 2);

        LOG.info(String.format("%s stream is ready for use", streamName));

        final ImpressionPutter putter = new ImpressionPutter(impFactory, kinesis, streamName);

        GeneralStreamWriter streamWriter = new GeneralStreamWriter(numberOfThreads, putter);

        streamWriter.doWrite();
    }
}
