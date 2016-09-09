package com.kinesis.connectors.s3.emitter;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.kinesis.connectors.s3.buffer.UnmodifiableBuffer;
import com.kinesis.datavis.utils.Ticker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by eugennekhai on 05/09/16.
 */
public class S3Emitter implements IEmitter<byte[]> {
    private static final Log LOG = LogFactory.getLog(S3Emitter.class);
    protected String s3Bucket;
    protected String s3Endpoint;

    protected final AmazonS3Client s3client;
    private String prefix;

    public S3Emitter(String type, String s3Bucket, String s3Endpoint) {
        this.prefix = "kinesis/" + type;
        this.s3Bucket = s3Bucket;
        this.s3Endpoint = s3Endpoint;

        DefaultAWSCredentialsProviderChain config = new DefaultAWSCredentialsProviderChain();
        s3client = new AmazonS3Client(config);
        if (s3Endpoint != null) {
            s3client.setEndpoint(s3Endpoint);
        }
    }

    protected String getS3FileName(String firstSeq, String lastSeq) {
        return prefix + Ticker.getInstance().s3Path() + firstSeq + "-" + lastSeq;
    }

    protected String getS3URI(String s3FileName) {
        return "s3://" + s3Bucket + "/" + s3FileName;
    }

    @Override
    public List<byte[]> emit(final UnmodifiableBuffer<byte[]> buffer) throws IOException {
        List<byte[]> records = buffer.getRecords();
        // Write all of the records to a compressed output stream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (byte[] record : records) {
            try {
                baos.write(record);
            } catch (Exception e) {
                LOG.error("Error writing record to output stream. Failing this emit attempt. Record: "
                                + Arrays.toString(record),
                        e);
                return buffer.getRecords();
            }
        }
        // Get the Amazon S3 filename
        String s3FileName = getS3FileName(buffer.getFirstSequenceNumber(), buffer.getLastSequenceNumber());
        String s3URI = getS3URI(s3FileName);
        try {
            ByteArrayInputStream object = new ByteArrayInputStream(baos.toByteArray());
            LOG.debug("Starting upload of file " + s3URI + " to Amazon S3 containing " + records.size() + " records.");
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(baos.size());
            s3client.putObject(s3Bucket, s3FileName, object, meta);
            LOG.info("Successfully emitted " + buffer.getRecords().size() + " records to Amazon S3 in " + s3URI);
            return Collections.emptyList();
        } catch (Exception e) {
            LOG.error("Caught exception when uploading file " + s3URI + "to Amazon S3. Failing this emit attempt.", e);
            return buffer.getRecords();
        }
    }

    @Override
    public void fail(List<byte[]> records) {
        for (byte[] record : records) {
            LOG.error("Record failed: " + Arrays.toString(record));
        }
    }

    @Override
    public void shutdown() {
        s3client.shutdown();
    }

}
