package com.kinesis.datavis.app;

import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.jdbc.dao.JDBCMappingDAO;
import com.jdbc.dao.MappingDAO;
import com.kinesis.datavis.kcl.processor.CountingRecordProcessorFactory;
import com.kinesis.datavis.kcl.persistence.Cleaner;
import com.kinesis.datavis.kcl.persistence.ddb.BidResponseCountPersister;
import com.kinesis.datavis.utils.AppUtils;
import com.kinesis.openrtb.BidResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.UnknownHostException;

/**
 * Created by eugennekhai on 26/08/16.
 */
public class BidResponseCounter extends CounterApp {
    private static final Log LOG = LogFactory.getLog(BidRequestCounter.class);

    // Count occurrences of HTTP referrer pairs over a range of 10 seconds
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
        if (args.length != 4) {
            System.err.println("Usage: " + BidRequestCounter.class.getSimpleName()
                    + " <application name> <stream name> <DynamoDB table name> <region>");
            System.exit(1);
        }

        String applicationName = args[0];
        String streamName = args[1];
        String countsTableName = args[2];
        Region region = AppUtils.parseRegion(args[3]);

        DynamoDBMapper mapper = createMapper(applicationName, streamName, countsTableName, region);

        MappingDAO mappingDAO = new JDBCMappingDAO();

        Cleaner cleaner = new Cleaner(mappingDAO);

        // Persist counts to DynamoDB
        BidResponseCountPersister persister = new BidResponseCountPersister(mapper, mappingDAO);

        IRecordProcessorFactory recordProcessor =
                new CountingRecordProcessorFactory<>(BidResponse.class,
                        persister,
                        mappingDAO,
                        COMPUTE_RANGE_FOR_COUNTS_IN_MILLIS,
                        COMPUTE_INTERVAL_IN_MILLIS);

        KinesisClientLibConfiguration kclConfig = buildConfig(applicationName, streamName, region);

        int exitCode = runWorker(recordProcessor, kclConfig);

        System.exit(exitCode);
    }
}
