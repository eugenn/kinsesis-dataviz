/*
 * Copyright 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.kinesis.datavis.kcl.processor;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.kinesis.connectors.s3.emitter.IEmitter;
import com.kinesis.datavis.kcl.persistence.CountPersister;
import com.kinesis.datavis.kcl.processor.type.TypeProcessor;

/**
 * Generates {@link CountingRecordProcessor}s for counting occurrences of unique values over a given range.
 *
 * @param <T> The type of records the processors this factory creates are capable of counting.
 */
public class CountingRecordProcessorFactory<T, C> implements IRecordProcessorFactory {

    private Class<T> recordType;
    private CountPersister<T, C> persister;
    private TypeProcessor typeProcessor;
    private IEmitter emitter;
    private int computeRangeInMillis;
    private int computeIntervalInMillis;
    private CountingRecordProcessorConfig config;

    public CountingRecordProcessorFactory(Class<T> recordType,
                                          CountPersister<T,C> persister,
                                          TypeProcessor<T> typeProcessor,
                                          IEmitter emitter,
                                          int computeRangeInMillis,
                                          int computeIntervalInMillis) {
        this(recordType, persister, typeProcessor, emitter, computeRangeInMillis, computeIntervalInMillis, new CountingRecordProcessorConfig());
    }

    public CountingRecordProcessorFactory(Class<T> recordType,
                                          CountPersister<T,C> persister,
                                          TypeProcessor<T> typeProcessor,
                                          IEmitter emitter,
                                          int computeRangeInMillis,
                                          int computeIntervalInMillis,
                                          CountingRecordProcessorConfig config) {
        this.recordType = recordType;
        this.persister = persister;
        this.typeProcessor = typeProcessor;
        this.emitter = emitter;
        this.computeRangeInMillis = computeRangeInMillis;
        this.computeIntervalInMillis = computeIntervalInMillis;
        this.config = config;
    }

    @Override
    public IRecordProcessor createProcessor() {
        return new CountingRecordProcessor<>(config,
                recordType,
                persister,
                typeProcessor,
                emitter,
                computeRangeInMillis,
                computeIntervalInMillis);
    }
}
