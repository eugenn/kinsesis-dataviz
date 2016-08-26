package com.amazonaws.services.kinesis.samples.datavis.writer;

import com.amazonaws.services.kinesis.samples.datavis.producer.Putter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by eugennekhai on 26/08/16.
 */
public class GeneralStreamWriter implements StreamWriter {
    private static final Log LOG = LogFactory.getLog(GeneralStreamWriter.class);
    private Putter putter;
    private int numberOfThreads;

    public GeneralStreamWriter(int numberOfThreads, Putter putter) {
        this.numberOfThreads = numberOfThreads;
        this.putter = putter;
    }

    /**
     * The amount of time to wait between records.
     * <p>
     * We want to send at most 10 records per second per thread so we'll delay 100ms between records.
     * This keeps the overall cost low for this sample.
     */
    private static final long DELAY_BETWEEN_RECORDS_IN_MILLIS = 100;

    public void doWrite() throws InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();

        Runnable bdSender = new Runnable() {
            @Override
            public void run() {
                try {
                    putter.sendRequestsIndefinitely(DELAY_BETWEEN_RECORDS_IN_MILLIS, TimeUnit.MILLISECONDS);
                } catch (Exception ex) {
                    LOG.warn("Thread encountered an error while sending records. Records will no longer be put by this thread.",
                            ex);
                }
            }
        };

        for (int i = 0; i < numberOfThreads; i++) {
            es.submit(bdSender);
        }

        LOG.info(String.format("Sending requests with a %dms delay between records with %d thread(s).",
                DELAY_BETWEEN_RECORDS_IN_MILLIS,
                numberOfThreads));

        es.shutdown();
        es.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

    }
}
