package com.amazonaws.services.kinesis.samples.datavis.writer;

/**
 * Created by eugennekhai on 26/08/16.
 */
public interface StreamWriter {
    void doWrite() throws InterruptedException;
}
