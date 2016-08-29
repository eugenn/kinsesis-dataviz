package com.amazonaws.services.kinesis.samples.datavis.writer;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.samples.datavis.producer.impression.ImpressionFactory;
import com.amazonaws.services.kinesis.samples.datavis.producer.impression.ImpressionPutter;
import com.amazonaws.services.kinesis.samples.datavis.utils.AppUtils;
import com.amazonaws.services.kinesis.samples.datavis.utils.StreamUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eugennekhai on 29/08/16.
 */
public class ImpressionStreamWriter {
    private static final Log LOG = LogFactory.getLog(BidWinStreamWriter.class);

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
        if (args.length != 3) {
            System.err.println("Usage: " + BidRequestStreamWriter.class.getSimpleName()
                    + " <number of threads> <stream name> <region>");
            System.exit(1);
        }

        init(args[0], args[1], args[2]);

    }

    private static void init(String arg1, String arg2, String arg3) throws InterruptedException {
        int numberOfThreads = Integer.parseInt(arg1);

        String streamName = arg2;

        Region region = AppUtils.parseRegion(arg3);

        AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

        ClientConfiguration clientConfig = AppUtils.configureUserAgentForSample(new ClientConfiguration());

        AmazonKinesis kinesis = new AmazonKinesisClient(credentialsProvider, clientConfig);
        kinesis.setRegion(region);

        // The more resources we declare the higher write IOPS we need on our DynamoDB table.
        // We write a record for each resource every interval.
        // If interval = 500ms, resource count = 7 we need: (1000/500 * 7) = 14 write IOPS minimum.
        List<String> bidRequestIds = new ArrayList<>();
//        bidRequestIds.add(UUID.randomUUID().toString());
//        bidRequestIds.add(UUID.randomUUID().toString());
//        bidRequestIds.add(UUID.randomUUID().toString());
        bidRequestIds.add("11111111111");
        bidRequestIds.add("22222222222");
        bidRequestIds.add("33333333333");

        List<BigDecimal> prices = new ArrayList<>();
        prices.add(BigDecimal.valueOf(5.33));
        prices.add(BigDecimal.valueOf(1.03));
        prices.add(BigDecimal.valueOf(0.53));
        prices.add(BigDecimal.valueOf(5.33));
        prices.add(BigDecimal.valueOf(10.223));
        prices.add(BigDecimal.valueOf(2.11));
        prices.add(BigDecimal.valueOf(3.33));
        prices.add(BigDecimal.valueOf(1.673));

        ImpressionFactory impFactory = new ImpressionFactory(bidRequestIds, prices, "impression");

        // Creates a stream to write to with 2 shards if it doesn't exist
        StreamUtils streamUtils = new StreamUtils(kinesis);
        streamUtils.createStreamIfNotExists(streamName, 2);

        LOG.info(String.format("%s stream is ready for use", streamName));

        final ImpressionPutter putter = new ImpressionPutter(impFactory, kinesis, streamName);

        GeneralStreamWriter streamWriter = new GeneralStreamWriter(numberOfThreads, putter);

        streamWriter.doWrite();
    }
}
