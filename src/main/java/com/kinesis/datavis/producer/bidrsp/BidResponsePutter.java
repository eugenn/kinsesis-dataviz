package com.kinesis.datavis.producer.bidrsp;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinesis.datavis.producer.Putter;
import com.kinesis.datavis.utils.Ticker;
import com.kinesis.openrtb.BidResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

/**
 * Created by eugennekhai on 25/08/16.
 */
public class BidResponsePutter implements Putter {
    private static final Log LOG = LogFactory.getLog(BidResponsePutter.class);

    private BidResponseFactory bidResponseFactory;
    private AmazonKinesis kinesis;
    private String streamName;

    private final ObjectMapper JSON = new ObjectMapper();

    public BidResponsePutter(BidResponseFactory bidResponseFactory, AmazonKinesis kinesis, String streamName) {
        if (bidResponseFactory == null) {
            throw new IllegalArgumentException("pairFactory must not be null");
        }
        if (kinesis == null) {
            throw new IllegalArgumentException("kinesis must not be null");
        }
        if (streamName == null || streamName.isEmpty()) {
            throw new IllegalArgumentException("streamName must not be null or empty");
        }
        this.bidResponseFactory = bidResponseFactory;
        this.kinesis = kinesis;
        this.streamName = streamName;
    }

    /**
     * Send a fixed number of HTTP Referrer pairs to Amazon Kinesis. This sends them sequentially.
     * If you require more throughput consider using multiple {@link }s.
     *
     * @param n The number of pairs to send to Amazon Kinesis.
     * @param delayBetweenRecords The amount of time to wait in between sending records. If this is <= 0 it will be
     *        ignored.
     * @param unitForDelay The unit of time to interpret the provided delay as.
     *
     * @throws InterruptedException Interrupted while waiting to send the next pair.
     */
    public void sendPairs(long n, long delayBetweenRecords, TimeUnit unitForDelay) throws InterruptedException {
        for (int i = 0; i < n && !Thread.currentThread().isInterrupted(); i++) {
            sendRq();
            Thread.sleep(unitForDelay.toMillis(delayBetweenRecords));
        }
    }

    /**
     * Continuously sends HTTP Referrer pairs to Amazon Kinesis sequentially. This will only stop if interrupted. If you
     * require more throughput consider using multiple {@link }s.
     *
     * @param delayBetweenRecords The amount of time to wait in between sending records. If this is <= 0 it will be
     *        ignored.
     * @param unitForDelay The unit of time to interpret the provided delay as.
     *
     * @throws InterruptedException Interrupted while waiting to send the next pair.
     */
    public void sendRequestsIndefinitely(long delayBetweenRecords, TimeUnit unitForDelay) throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            sendRq();
            if (delayBetweenRecords > 0) {
                Thread.sleep(unitForDelay.toMillis(delayBetweenRecords));
            }
        }
    }

    /**
     * Send a single pair to Amazon Kinesis using PutRecord.
     */
    private void sendRq() {
        BidResponse rec = bidResponseFactory.create();
        byte[] bytes;
        try {
            bytes = JSON.writeValueAsBytes(rec);
        } catch (IOException e) {
            LOG.warn("Skipping pair. Unable to serialize: '" + rec + "'", e);
            return;
        }

        PutRecordRequest putRecord = new PutRecordRequest();
        putRecord.setStreamName(streamName);

        putRecord.setPartitionKey(Ticker.getInstance().hashKey());
        putRecord.setData(ByteBuffer.wrap(bytes));

        putRecord.setSequenceNumberForOrdering(null);

        try {
            kinesis.putRecord(putRecord);
        } catch (ProvisionedThroughputExceededException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Thread %s's Throughput exceeded. Waiting 10ms", Thread.currentThread().getName()));
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } catch (AmazonClientException ex) {
            LOG.warn("Error sending record to Amazon Kinesis.", ex);
        }

//        System.out.println(rec.getBannerId() + "    " + rec.getAudienceId());
    }
}
