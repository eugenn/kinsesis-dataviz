package com.kinesis.datavis.app;

import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.jdbc.dao.JDBCMappingDAO;
import com.kinesis.connectors.s3.buffer.FlushBuffer;
import com.kinesis.connectors.s3.emitter.S3Emitter;
import com.kinesis.datavis.kcl.persistence.ddb.BidWinCountPersister;
import com.kinesis.datavis.kcl.processor.TwinCountingProcessorFactory;
import com.kinesis.datavis.kcl.processor.type.CommonTypeProcessor;
import com.kinesis.datavis.kcl.processor.type.TypeProcessor;
import com.kinesis.datavis.model.record.BidWinRec;
import com.kinesis.datavis.utils.AppProperties;
import com.kinesis.datavis.utils.AppUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.UnknownHostException;

/**
 * Created by eugennekhai on 25/08/16.
 */
public class BidWinCounter extends CounterApp {
    private static final Log LOG = LogFactory.getLog(BidWinCounter.class);

    // Count occurrences over a range of 1 seconds
    private static final int COMPUTE_RANGE_FOR_COUNTS_IN_MILLIS = 1000;
    // Update the counts every 1000 msec
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

        AppProperties appProps = new AppProperties("bidwin", path);

        String applicationName = appProps.appName();
        String streamName = appProps.streamName();
        String countsTableName = appProps.countTable();
        Region region = AppUtils.parseRegion(appProps.getRegion());

        DynamoDBMapper mapper = createMapper(applicationName, streamName, countsTableName, region);

        TypeProcessor<BidWinRec> typeProcessor =
                new CommonTypeProcessor<>(new JDBCMappingDAO(appProps.dbUrl(), appProps.dbUser(), appProps.dbPassword()), new FlushBuffer<>());

        IRecordProcessorFactory recordProcessor =
                new TwinCountingProcessorFactory<>(BidWinRec.class,
                        new BidWinCountPersister(mapper),
                        typeProcessor,
                        new S3Emitter("bidwin"),
                        COMPUTE_RANGE_FOR_COUNTS_IN_MILLIS,
                        COMPUTE_INTERVAL_IN_MILLIS);

        KinesisClientLibConfiguration kclConfig = buildConfig(applicationName, streamName, region);

        int exitCode = runWorker(recordProcessor, kclConfig);

        System.exit(exitCode);

    }
}
