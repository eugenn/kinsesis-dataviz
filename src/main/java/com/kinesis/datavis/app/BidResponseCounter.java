package com.kinesis.datavis.app;

import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.jdbc.dao.JDBCMappingDAO;
import com.jdbc.dao.MappingDAO;
import com.jdbc.vo.Mapping;
import com.kinesis.connectors.s3.buffer.FlushBuffer;
import com.kinesis.connectors.s3.emitter.S3Emitter;
import com.kinesis.datavis.kcl.persistence.Cleaner;
import com.kinesis.datavis.kcl.persistence.PersisterThread;
import com.kinesis.datavis.kcl.persistence.ddb.BidResponseCountPersister;
import com.kinesis.datavis.kcl.processor.CountingRecordProcessorFactory;
import com.kinesis.datavis.kcl.processor.type.BidResponseProcessor;
import com.kinesis.datavis.kcl.processor.type.TypeProcessor;
import com.kinesis.datavis.utils.AppProperties;
import com.kinesis.datavis.utils.AppUtils;
import com.kinesis.openrtb.BidResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by eugennekhai on 26/08/16.
 */
public class BidResponseCounter extends CounterApp {
    private static final Log LOG = LogFactory.getLog(BidRequestCounter.class);

    // Count occurrences of HTTP referrer pairs over a range of 10 seconds
    private static final int COMPUTE_RANGE_FOR_COUNTS_IN_MILLIS = 10000;
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

        AppProperties appProps = new AppProperties("bidrsp", path);

        String applicationName = appProps.appName();
        String streamName = appProps.streamName();
        String countsTableName = appProps.countTable();

        String s3Bucket = appProps.s3Bucket();
        String s3Endpoint = appProps.s3Endpoint();

        Region region = AppUtils.parseRegion(appProps.getRegion());

        DynamoDBMapper mapper = createMapper(streamName, countsTableName, region);

        MappingDAO mappingDAO = new JDBCMappingDAO(appProps.dbUrl(), appProps.dbUser(), appProps.dbPassword());

        Cleaner cleaner = new Cleaner(mappingDAO);

        BlockingQueue<Mapping> mappingsBuff = new LinkedBlockingQueue<>(PersisterThread.MAX_COUNTS_IN_MEMORY * 2);

        TypeProcessor<BidResponse> typeProcessor = new BidResponseProcessor(mappingDAO, new FlushBuffer<>(), mappingsBuff);

        IRecordProcessorFactory recordProcessor =
                new CountingRecordProcessorFactory<>(BidResponse.class,
                        new BidResponseCountPersister(mapper),
                        typeProcessor,
                        new S3Emitter("bdresponse", s3Bucket, s3Endpoint),
                        COMPUTE_RANGE_FOR_COUNTS_IN_MILLIS,
                        COMPUTE_INTERVAL_IN_MILLIS);

        KinesisClientLibConfiguration kclConfig = buildConfig(applicationName, streamName, region);

        int exitCode = runWorker(recordProcessor, kclConfig);

        System.exit(exitCode);
    }
}
