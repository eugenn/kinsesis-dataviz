package com.kinesis.datavis.kcl.adder;

import com.kinesis.datavis.utils.ReflectionUtil;

/**
 * Created by eugennekhai on 29/08/16.
 */
public class BucketBasedAdder<ObjectType> extends DefaultBasketAdder<ObjectType> {

    /**
     * Create a new counter with a fixed number of buckets.
     *
     * @param maxBuckets Total buckets this counter will use.
     */
    public BucketBasedAdder(int maxBuckets) {
        super(maxBuckets);
    }

    /**
     * Increment the count of the object for a specific bucket index.
     *
     * @param obj Object whose count should be updated.
     * @param bucket Index of bucket to increment.
     * @return The new count for that object at the bucket index provided.
     */
    public double sum(ObjectType obj, int bucket) {
        double[] counts = objectSums.get(obj);
        if (counts == null) {
            counts = new double[maxBuckets];
            objectSums.put(obj, counts);
        }
        counts[bucket] += ReflectionUtil.getDValue(obj, "getWinPrice");
        return counts[bucket];
    }


}
