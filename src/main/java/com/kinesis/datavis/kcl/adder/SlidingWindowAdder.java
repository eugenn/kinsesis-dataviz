package com.kinesis.datavis.kcl.adder;

import com.kinesis.datavis.kcl.counter.BucketBasedCounter;

import java.util.Map;

/**
 * Created by eugennekhai on 29/08/16.
 */
public class SlidingWindowAdder<ObjectType> {

    private BucketBasedAdder<ObjectType> adder;

    private int windowSize;
    private int headBucket;
    private int tailBucket;
    // Keep track of the total window advances so we can answer the question: Is this window full?
    private int totalAdvances;

    public SlidingWindowAdder(int windowSize) {
        if (windowSize < 1) {
            throw new IllegalArgumentException("windowSize must be >= 1");
        }
        this.windowSize = windowSize;

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



    public void sum(ObjectType obj, double val) {
        adder.sum(obj, headBucket, val);
    }

    /**
     * Get the counts for all objects across all buckets.
     *
     * @return A mapping of ObjectType -> total count across all buckets.
     */
    public Map<ObjectType, Double> getCounts() {
        return adder.getCounts();
    }

    /**
     * Get the counts for all objects across all buckets.
     *
     * @return A mapping of ObjectType -> total count across all buckets.
     */
    public Map<ObjectType, Double> getSums() {
        return adder.getSum();
    }

    /**
     * Advance the window "one bucket". This will remove the oldest bucket and any count stored in it.
     */
    public void advanceWindow() {
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
        adder.pruneEmptyObjects();
    }
}
