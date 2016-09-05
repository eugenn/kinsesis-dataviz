package com.kinesis.connectors.s3.buffer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by eugennekhai on 05/09/16.
 */
public class FlushBuffer<T> implements IBuffer<T> {

    private final long bytesPerFlush = 1048576;
    private final long numMessagesToBuffer = 25;
    private final long millisecondsToBuffer = 3600000;

    private final List<T> buffer = new LinkedList<T>();
    private final AtomicLong byteCount = new AtomicLong();

    private String firstSequenceNumber;
    private String lastSequenceNumber;

    private long previousFlushTimeMillisecond;

    public FlushBuffer() {
        previousFlushTimeMillisecond = getCurrentTimeMilliseconds();
    }

    @Override
    public long getBytesToBuffer() {
        return bytesPerFlush;
    }

    @Override
    public long getNumRecordsToBuffer() {
        return numMessagesToBuffer;
    }

    @Override
    public long getMillisecondsToBuffer() {
        return millisecondsToBuffer;
    }

    @Override
    public void consumeRecord(T record, int recordSize, String sequenceNumber) {
        if (buffer.isEmpty()) {
            firstSequenceNumber = sequenceNumber;
        }
        lastSequenceNumber = sequenceNumber;
        buffer.add(record);
        byteCount.addAndGet(recordSize);
    }

    @Override
    public void clear() {
        buffer.clear();
        byteCount.set(0);
        previousFlushTimeMillisecond = getCurrentTimeMilliseconds();
    }

    @Override
    public String getFirstSequenceNumber() {
        return firstSequenceNumber;
    }

    @Override
    public String getLastSequenceNumber() {
        return lastSequenceNumber;
    }

    /**
     * By default, we flush once we have exceeded the number of messages or maximum bytes to buffer.
     * However, subclasses can use their own means to determine if they should flush.
     *
     * @return true if either the number of records in the buffer exceeds max number of records or
     * the size of the buffer exceeds the max number of bytes in the buffer.
     */
    @Override
    public boolean shouldFlush() {
        long timelapseMillisecond = getCurrentTimeMilliseconds() - previousFlushTimeMillisecond;
        return (!buffer.isEmpty())
                && ((buffer.size() >= getNumRecordsToBuffer()) || (byteCount.get() >= getBytesToBuffer()) || (timelapseMillisecond >= getMillisecondsToBuffer()));
    }

    @Override
    public List<T> getRecords() {
        return buffer;
    }

    // This method has protected access for unit testing purposes.
    protected long getCurrentTimeMilliseconds() {
        return System.currentTimeMillis();
    }

}
