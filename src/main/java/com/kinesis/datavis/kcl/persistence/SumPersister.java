package com.kinesis.datavis.kcl.persistence;

import java.util.Collection;
import java.util.Map;

/**
 * Created by eugennekhai on 29/08/16.
 */
public interface SumPersister<T, C>  {
    /**
     * Persist the map of objects to counts.
     *
     * @param objectSum
     */
    Collection<C> persistPrice(Map<T, Double> objectSum);

}
