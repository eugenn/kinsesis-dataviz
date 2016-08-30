package com.kinesis.datavis.kcl;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.kinesis.datavis.kcl.persistence.CountPersister;

/**
 * Created by eugennekhai on 30/08/16.
 */
public class TwinCountingProcessorFactory<T, C> implements IRecordProcessorFactory {

    private Class<T> recordType;
    private CountPersister<T, C> persister;
    private int computeRangeInMillis;
    private int computeIntervalInMillis;
    private CountingRecordProcessorConfig config;

    /**
     * Creates a new factory that uses the default configuration values for each
     * processor it creates.
     *
     * @see #TwinCountingProcessorFactory(Class, CountPersister, int, int)
     */
    public TwinCountingProcessorFactory(Class<T> recordType,
                                          CountPersister<T,C> persister,
                                          int computeRangeInMillis,
                                          int computeIntervalInMillis) {
        this(recordType, persister, computeRangeInMillis, computeIntervalInMillis, new CountingRecordProcessorConfig());
    }

    /**
     * Create a new factory that produces counting record processors that sum counts over a range and update those
     * counts at each interval.
     *
     * @param recordType              The type of records the processors this factory creates are capable of counting.
     * @param persister               Persister to use for storing the counts.
     * @param computeRangeInMillis    Range, in milliseconds, to compute the count across.
     * @param computeIntervalInMillis Milliseconds between count updates. This is the frequency at which the persister
     *                                will be called.
     * @param config                  The configuration to use for each created counting record processor.
     * @throws IllegalArgumentException if computeRangeInMillis or computeIntervalInMillis are not greater than 0 or
     *                                  computeRangeInMillis is not evenly divisible by computeIntervalInMillis.
     */
    public TwinCountingProcessorFactory(Class<T> recordType,
                                          CountPersister<T,C> persister,
                                          int computeRangeInMillis,
                                          int computeIntervalInMillis,
                                          CountingRecordProcessorConfig config) {
        this.recordType = recordType;
        this.persister = persister;
        this.computeRangeInMillis = computeRangeInMillis;
        this.computeIntervalInMillis = computeIntervalInMillis;
        this.config = config;
    }

    /**
     * Creates a counting record processor that sums counts over the provided compute range and updates those counts
     * every interval.
     */
    @Override
    public IRecordProcessor createProcessor() {
        return new TwinCountingRecordProcessor<>(config,
                recordType,
                persister,
                computeRangeInMillis,
                computeIntervalInMillis);
    }
}
