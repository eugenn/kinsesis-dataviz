package com.amazonaws.services.kinesis.samples.datavis.app;

import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.samples.datavis.kcl.CountingRecordProcessorFactory;
import com.amazonaws.services.kinesis.samples.datavis.kcl.persistence.ddb.DynamoDBBidWinCountPersister;
import com.amazonaws.services.kinesis.samples.datavis.model.record.BidWinRec;
import com.amazonaws.services.kinesis.samples.datavis.utils.SampleUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.UnknownHostException;

/**
 * Created by eugennekhai on 25/08/16.
 */
public class BidWinCounter extends CounterApp {
    private static final Log LOG = LogFactory.getLog(BidWinCounter.class);

    // Count occurrences of HTTP referrer pairs over a range of 10 seconds
    private static final int COMPUTE_RANGE_FOR_COUNTS_IN_MILLIS = 1000;
    // Update the counts every 1 second
    private static final int COMPUTE_INTERVAL_IN_MILLIS = 1000;

    /**
     * Start the Kinesis Client application.
     *
     * @param args Expecting 4 arguments: Application name to use for the Kinesis Client Application, Stream name to
     *        read from, DynamoDB table name to persist counts into, and the AWS region in which these resources
     *        exist or should be created.
     */
    public static void main(String[] args) throws UnknownHostException {
        if (args.length != 4) {
            System.err.println("Usage: " + BidRequestCounter.class.getSimpleName()
                    + " <application name> <stream name> <DynamoDB table name> <region>");
            System.exit(1);
        }

        String applicationName = args[0];
        String streamName = args[1];
        String countsTableName = args[2];
        Region region = SampleUtils.parseRegion(args[3]);

        DynamoDBMapper mapper = createMapper(applicationName, streamName, countsTableName, region);

        // Persist counts to DynamoDB
        DynamoDBBidWinCountPersister persister =
                new DynamoDBBidWinCountPersister(mapper);


        IRecordProcessorFactory recordProcessor =
                new CountingRecordProcessorFactory<BidWinRec>(BidWinRec.class,
                        persister,
                        COMPUTE_RANGE_FOR_COUNTS_IN_MILLIS,
                        COMPUTE_INTERVAL_IN_MILLIS);

        KinesisClientLibConfiguration kclConfig = buildConfig(applicationName, streamName, region);

        int exitCode = runWorker(recordProcessor, kclConfig);

        System.exit(exitCode);

    }
}
