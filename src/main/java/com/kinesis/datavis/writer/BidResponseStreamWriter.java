package com.kinesis.datavis.writer;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.kinesis.datavis.producer.bidrsp.BidResponseFactory;
import com.kinesis.datavis.producer.bidrsp.BidResponsePutter;
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
public class BidResponseStreamWriter {
    private static final Log LOG = LogFactory.getLog(BidWinStreamWriter.class);

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

        AppProperties appProps = new AppProperties("bidrsp", args[1]);

        String streamName = appProps.streamName();

        Region region = AppUtils.parseRegion(appProps.getRegion());

        AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

        ClientConfiguration clientConfig = AppUtils.configureUserAgentForSample(new ClientConfiguration());

        AmazonKinesis kinesis = new AmazonKinesisClient(credentialsProvider, clientConfig);
        kinesis.setRegion(region);


        List<String> bidRqs = new ArrayList<>();
//        bidRqs.add("11111111111");
//        bidRqs.add("22222222222");
//        bidRqs.add("33333333333");
//        bidRqs.add("44444444444");

        bidRqs.add("8e7f090c-6710-4cd4-b872-c52d4af873c7");


        List<String> bannerIds = new ArrayList<>();
        bannerIds.add("11111111111");
        bannerIds.add("22222222222");
//        bannerIds.add("33333333333");
//        bannerIds.add("44444444444");

        List<String> audienceIds = new ArrayList<>();
        audienceIds.add("female");
        audienceIds.add("male");
//        bannerIds.add("33333333333");
//        bannerIds.add("44444444444");


        BidResponseFactory responseFactory = new BidResponseFactory(bidRqs, bannerIds, audienceIds);

        // Creates a stream to write to with 2 shards if it doesn't exist
        StreamUtils streamUtils = new StreamUtils(kinesis);
        streamUtils.createStreamIfNotExists(streamName, 2);

        LOG.info(String.format("%s stream is ready for use", streamName));

        final BidResponsePutter putter = new BidResponsePutter(responseFactory, kinesis, streamName);

        GeneralStreamWriter streamWriter = new GeneralStreamWriter(numberOfThreads, putter);

        streamWriter.doWrite();
    }
}
