package com.kinesis.connectors.s3.buffer;

import java.util.List;

/**
 * Created by eugennekhai on 05/09/16.
 */
public interface IBuffer<T> {
    /**
     * Get the byte size limit of data stored in the buffer before the records are flushed to the
     * emitter
     *
     * @return byte size limit of buffer
     */
    public long getBytesToBuffer();

    /**
     * Get the record number limit of data stored in the buffer before the records are flushed to
     * the emitter
     *
     * @return record number limit of buffer
     */
    public long getNumRecordsToBuffer();

    /**
     * Get the time limit in milliseconds before the records are flushed to the emitter
     *
     * @return time limit in milleseconds
     */
    public long getMillisecondsToBuffer();

    /**
     * Returns true if the buffer is full and stored records should be sent to the emitter
     *
     * @return true if records should be sent to the emitter followed by clearing the buffer
     */
    public boolean shouldFlush();

    /**
     * Stores the record in the buffer
     *
     * @param record
     *        record to be processed
     * @param recordBytes
     *        size of the record data in bytes
     * @param sequenceNumber
     *        Amazon Kinesis sequence identifier
     */
    public void consumeRecord(T record, int recordBytes, String sequenceNumber);

    /**
     * Clears the buffer
     */
    public void clear();

    /**
     * Get the sequence number of the first record stored in the buffer. Used for bookkeeping and
     * uniquely identifying items in the buffer.
     *
     * @return the sequence number of the first record stored in the buffer
     */
    public String getFirstSequenceNumber();

    /**
     * Get the sequence number of the last record stored in the buffer. Used for bookkeeping and
     * uniquely identifying items in the buffer.
     *
     * @return the sequence number of the last record stored in the buffer
     */
    public String getLastSequenceNumber();

    /**
     * Get the records stored in the buffer
     *
     * @return the records stored in the buffer
     */
    public List<T> getRecords();
}
