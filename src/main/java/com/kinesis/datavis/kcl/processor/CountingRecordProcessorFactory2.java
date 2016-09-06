package com.kinesis.datavis.kcl.processor;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.jdbc.dao.MappingDAO;
import com.kinesis.connectors.s3.emitter.IEmitter;
import com.kinesis.datavis.kcl.persistence.CountPersister;

/**
 * Created by eugennekhai on 06/09/16.
 */
public class CountingRecordProcessorFactory2<T, C> implements IRecordProcessorFactory {

    private Class<T> recordType;
    private CountPersister<T, C> persister;
    private MappingDAO mappingDAO;
    private IEmitter emitter;
    private int computeRangeInMillis;
    private int computeIntervalInMillis;
    private CountingRecordProcessorConfig config;

    public CountingRecordProcessorFactory2(Class<T> recordType,
                                          CountPersister<T,C> persister,
                                          MappingDAO mappingDAO,
                                          IEmitter emitter,
                                          int computeRangeInMillis,
                                          int computeIntervalInMillis) {
        this(recordType, persister, mappingDAO, emitter, computeRangeInMillis, computeIntervalInMillis, new CountingRecordProcessorConfig());
    }

    public CountingRecordProcessorFactory2(Class<T> recordType,
                                          CountPersister<T,C> persister,
                                          MappingDAO mappingDAO,
                                          IEmitter emitter,
                                          int computeRangeInMillis,
                                          int computeIntervalInMillis,
                                          CountingRecordProcessorConfig config) {
        this.recordType = recordType;
        this.persister = persister;
        this.mappingDAO = mappingDAO;
        this.emitter = emitter;
        this.computeRangeInMillis = computeRangeInMillis;
        this.computeIntervalInMillis = computeIntervalInMillis;
        this.config = config;
    }

    @Override
    public IRecordProcessor createProcessor() {
        return new CountingRecordProcessor2<>(config,
                recordType,
                persister,
                mappingDAO,
                emitter,
                computeRangeInMillis,
                computeIntervalInMillis);
    }
}
