package com.amazonaws.services.kinesis.samples.datavis.producer;

import java.util.concurrent.TimeUnit;

/**
 * Created by eugennekhai on 26/08/16.
 */
public interface Putter {
    void sendRequestsIndefinitely(long delayBetweenRecords, TimeUnit unitForDelay) throws InterruptedException;
}
