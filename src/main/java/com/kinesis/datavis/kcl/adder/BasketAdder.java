package com.kinesis.datavis.kcl.adder;

import java.util.Map;

/**
 * Created by eugennekhai on 30/08/16.
 */
public interface BasketAdder<ObjectType> {
    double sum(ObjectType obj, int bucket);
    Map<ObjectType, Double> getSum();
}
