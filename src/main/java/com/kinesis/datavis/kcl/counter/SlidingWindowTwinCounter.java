package com.kinesis.datavis.kcl.counter;

import com.kinesis.datavis.kcl.adder.BucketBasedAdder;

import java.util.Map;

/**
 * Created by eugennekhai on 30/08/16.
 */
public class SlidingWindowTwinCounter<ObjectType> {
    private BucketBasedCounter<ObjectType> counter;
    private BucketBasedAdder<ObjectType> adder;

    private int windowSize;
    private int headBucket;
    private int tailBucket;
    // Keep track of the total window advances so we can answer the question: Is this window full?
    private int totalAdvances;

    public SlidingWindowTwinCounter(int windowSize) {
        if (windowSize < 1) {
            throw new IllegalArgumentException("windowSize must be >= 1");
        }

        this.windowSize = windowSize;

        this.counter = new BucketBasedCounter<>(windowSize);
        this.adder = new BucketBasedAdder<>(windowSize);

        headBucket = 0;
        tailBucket = getNextBucket(headBucket);
    }

    /**
     * Determine which bucket comes "after" a given bucket. This handles the edge case where the bucket provided is at
     * the end of the list of buckets.
     *
     * @param bucket The index of the bucket to start at.
     * @return The bucket that is logically after the given bucket.
     */
    private int getNextBucket(int bucket) {
        return (bucket + 1) % windowSize;
    }

    /**
     * Increment the count for an object in the current bucket by 1.
     *
     * @param obj Object whose count should be incremented.
     */
    public void increment(ObjectType obj) {
        counter.increment(obj, headBucket);
    }

    /**
     * Increment the count for an object in the current bucket by 1.
     *
     * @param obj Object whose count should be incremented.
     */
    public void sum(ObjectType obj) {
        adder.sum(obj, headBucket);
    }

    /**
     * Get the counts for all objects across all buckets.
     *
     * @return A mapping of ObjectType -> total count across all buckets.
     */
    public Map<ObjectType, Long> getCounts() {
        return counter.getCounts();
    }

    /**
     * Get the counts for all objects across all buckets.
     *
     * @return A mapping of ObjectType -> total count across all buckets.
     */
    public Map<ObjectType, Double> getSum() {
        return adder.getSum();
    }

    /**
     * Advance the window "one bucket". This will remove the oldest bucket and any count stored in it.
     */
    public void advanceWindow() {
        counter.clearBucket(tailBucket);
        adder.clearBucket(tailBucket);

        headBucket = tailBucket;
        tailBucket = getNextBucket(headBucket);

        if (!isWindowFull()) {
            // Only increment if our window has not yet filled up.
            totalAdvances++;
        }
    }

    /**
     * Check if we've advanced our window enough times to have completely filled all buckets.
     *
     * @return {@code true} if the window is full.
     */
    public boolean isWindowFull() {
        return windowSize <= totalAdvances;
    }

    /**
     * @see BucketBasedCounter#pruneEmptyObjects()
     */
    public void pruneEmptyObjects() {
        counter.pruneEmptyObjects();
        adder.pruneEmptyObjects();
    }
}
