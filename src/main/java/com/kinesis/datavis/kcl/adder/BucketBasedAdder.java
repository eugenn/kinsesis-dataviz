package com.kinesis.datavis.kcl.adder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eugennekhai on 29/08/16.
 */
public class BucketBasedAdder<ObjectType> {
    private Map<ObjectType, double[]> objectSums;
    private int maxBuckets;

    /**
     * Create a new counter with a fixed number of buckets.
     *
     * @param maxBuckets Total buckets this counter will use.
     */
    public BucketBasedAdder(int maxBuckets) {
        if (maxBuckets < 1) {
            throw new IllegalArgumentException("maxBuckets must be >= 1");
        }
        objectSums = new HashMap<>();
        this.maxBuckets = maxBuckets;
    }

    /**
     * Increment the count of the object for a specific bucket index.
     *
     * @param obj Object whose count should be updated.
     * @param bucket Index of bucket to increment.
     * @return The new count for that object at the bucket index provided.
     */
    public double sum(ObjectType obj, int bucket, double val) {
        double[] counts = objectSums.get(obj);
        if (counts == null) {
            counts = new double[maxBuckets];
            objectSums.put(obj, counts);
        }
        return counts[bucket] + val;
    }

    /**
     * Computes the total count for all objects across all buckets.
     *
     * @return A mapping of object to total count across all buckets.
     */
    public Map<ObjectType, Double> getCounts() {
        Map<ObjectType, Double> count = new HashMap<>();

        for (Map.Entry<ObjectType, double[]> entry : objectSums.entrySet()) {
            count.put(entry.getKey(), calculateTotal(entry.getValue()));
        }

        return count;
    }

    /**
     * Computes the total count for all objects across all buckets.
     *
     * @return A mapping of object to total count across all buckets.
     */
    public Map<ObjectType, Double> getSum() {
        Map<ObjectType, Double> count = new HashMap<>();

        for (Map.Entry<ObjectType, double[]> entry : objectSums.entrySet()) {
            count.put(entry.getKey(), calculateSumTotal(entry.getValue()));
        }

        return count;
    }

    /**
     * Calculates the sum total of occurrences across all bucket counts.
     *
     * @param counts A set of count buckets from the same object.
     * @return The sum of all counts in the buckets.
     */
    private double calculateTotal(double[] counts) {
        double total = 0;
        for (double count : counts) {
            total += count;
        }
        return total;
    }

    private double calculateSumTotal(double[] counts) {
        double total = 0;
        for (double count : counts) {
            total += count;
        }
        return total;
    }

    /**
     * Remove any objects whose buckets total 0.
     */
    public void pruneEmptyObjects() {
        List<ObjectType> toBePruned = new ArrayList<>();
        for (Map.Entry<ObjectType, double[]> entry : objectSums.entrySet()) {
            // Remove objects whose total counts are 0
            if (calculateTotal(entry.getValue()) == 0) {
                toBePruned.add(entry.getKey());
            }
        }
        for (ObjectType prune : toBePruned) {
            objectSums.remove(prune);
        }
    }

    /**
     * Clears all object counts for the given bucket. If you wish to remove objects that no longer have any counts in
     * any bucket use {@link #pruneEmptyObjects()}.
     *
     * @param bucket The index of the bucket to clear.
     */
    public void clearBucket(int bucket) {
        for (double[] counts : objectSums.values()) {
            counts[bucket] = 0;
        }
    }
}
