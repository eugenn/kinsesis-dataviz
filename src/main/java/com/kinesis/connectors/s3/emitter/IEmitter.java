package com.kinesis.connectors.s3.emitter;

import com.kinesis.connectors.s3.buffer.UnmodifiableBuffer;

import java.io.IOException;
import java.util.List;

/**
 * Created by eugennekhai on 05/09/16.
 */
public interface IEmitter<T> {

    /**
     * Invoked when the buffer is full. This method emits the set of filtered records. It should
     * return a list of records that were not emitted successfully. Returning
     * Collections.emptyList() is considered a success.
     *
     * @param buffer
     *        The full buffer of records
     * @throws IOException
     *         A failure was reached that is not recoverable, no retry will occur and the fail
     *         method will be called
     * @return A list of records that failed to emit to be retried
     */
    List<T> emit(UnmodifiableBuffer<T> buffer) throws IOException;

    /**
     * This method defines how to handle a set of records that cannot successfully be emitted.
     *
     * @param records
     *        a list of records that were not successfully emitted
     */
    void fail(List<T> records);

    /**
     * This method is called when the KinesisConnectorRecordProcessor is shutdown. It should close
     * any existing client connections.
     */
    void shutdown();
}
