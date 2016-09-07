package com.kinesis.datavis.kcl.processor.type;

import com.amazonaws.services.kinesis.model.Record;
import com.kinesis.connectors.s3.buffer.IBuffer;

/**
 * Created by eugennekhai on 07/09/16.
 */
public interface TypeProcessor<T> {
    T process(Record r, T obj);
    IBuffer getBuffer();
}
