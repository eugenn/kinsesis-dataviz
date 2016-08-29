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

package com.kinesis.datavis.kcl;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.kinesis.datavis.kcl.persistence.CountPersister;

/**
 * Generates {@link CountingRecordProcessor}s for counting occurrences of unique values over a given range.
 *
 * @param <T> The type of records the processors this factory creates are capable of counting.
 */
public class CountingRecordProcessorFactory<T, C> implements IRecordProcessorFactory {

    private Class<T> recordType;
    private CountPersister<T, C> persister;
    private int computeRangeInMillis;
    private int computeIntervalInMillis;
    private CountingRecordProcessorConfig config;

    /**
     * Creates a new factory that uses the default configuration values for each
     * processor it creates.
     *
     * @see #CountingRecordProcessorFactory(Class, CountPersister, int, int)
     */
    public CountingRecordProcessorFactory(Class<T> recordType,
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
    public CountingRecordProcessorFactory(Class<T> recordType,
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
        return new CountingRecordProcessor<>(config,
                recordType,
                persister,
                computeRangeInMillis,
                computeIntervalInMillis);
    }
}
