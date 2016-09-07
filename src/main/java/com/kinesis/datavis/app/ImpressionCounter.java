package com.kinesis.datavis.app;

import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.jdbc.dao.JDBCMappingDAO;
import com.kinesis.connectors.s3.buffer.FlushBuffer;
import com.kinesis.connectors.s3.emitter.S3Emitter;
import com.kinesis.datavis.kcl.persistence.ddb.ImpressionCountPersister;
import com.kinesis.datavis.kcl.processor.TwinCountingProcessorFactory;
import com.kinesis.datavis.kcl.processor.type.CommonTypeProcessor;
import com.kinesis.datavis.kcl.processor.type.TypeProcessor;
import com.kinesis.datavis.model.record.ImpressionRec;
import com.kinesis.datavis.utils.AppProperties;
import com.kinesis.datavis.utils.AppUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.UnknownHostException;

/**
 * Created by eugennekhai on 26/08/16.
 */
public class ImpressionCounter extends CounterApp {
    private static final Log LOG = LogFactory.getLog(BidWinCounter.class);

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
        String path = args[0];

        AppProperties appProps = new AppProperties("impression", path);

        String applicationName = appProps.appName();
        String streamName = appProps.streamName();
        String countsTableName = appProps.countTable();
        Region region = AppUtils.parseRegion(appProps.getRegion());

        DynamoDBMapper mapper = createMapper(applicationName, streamName, countsTableName, region);

        TypeProcessor<ImpressionRec> typeProcessor =
                new CommonTypeProcessor<>(new JDBCMappingDAO(appProps.dbUrl(), appProps.dbUser(), appProps.dbPassword()), new FlushBuffer<>());

        IRecordProcessorFactory recordProcessor =
                new TwinCountingProcessorFactory<>(ImpressionRec.class,
                        new ImpressionCountPersister(mapper),
                        typeProcessor,
                        new S3Emitter("impression"),
                        COMPUTE_RANGE_FOR_COUNTS_IN_MILLIS,
                        COMPUTE_INTERVAL_IN_MILLIS);

        KinesisClientLibConfiguration kclConfig = buildConfig(applicationName, streamName, region);

        int exitCode = runWorker(recordProcessor, kclConfig);

        System.exit(exitCode);

    }
}
