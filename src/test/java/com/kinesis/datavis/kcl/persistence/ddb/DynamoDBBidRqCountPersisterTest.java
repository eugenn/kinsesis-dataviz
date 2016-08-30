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

package com.kinesis.datavis.kcl.persistence.ddb;

public class DynamoDBBidRqCountPersisterTest {

//    private BidRqCountPersister persister;
//    private DynamoDBMapper mapper;
//
//    // All methods in this test class timeout after 1 second to prevent a bug when dequeuing from the blocking queue to
//    // prevent our test suite from hanging.
//    @Rule
//    public TestRule globalTimeout = new Timeout(1000);
//
//    @Before
//    public void init() {
//        mapper = mock(DynamoDBMapper.class);
//        persister = new BidRqCountPersister(mapper);
//    }
//
//    @Test
//    public void GIVEN_persistCalledWithCounts_WHEN_sendQueueToDynamoDB_THEN_mapperCalledWithCounts()
//        throws InterruptedException {
//
//        final BidRequestRec pair = new BidRequestRec();
//        pair.setWh("a");
//        pair.setType("b");
//        final long count = 1L;
////        persister.persistCounter(Collections.singletonMap(pair, count));
////
////        persister.sendQueueToDynamoDB(new ArrayList<HttpReferrerPairsCount>());
//
//        @SuppressWarnings({ "unchecked", "rawtypes" })
//        ArgumentCaptor<List<BidRequestCount>> pairsCountCaptor = ArgumentCaptor.forClass((Class) List.class);
//        @SuppressWarnings({ "unchecked", "rawtypes" })
//        ArgumentCaptor<List<BidRequestCount>> ignoredCaptor = ArgumentCaptor.forClass((Class) List.class);
//
//        // Capture the arguments passed to the mapper when persisting counts to DynamoDB
//        verify(mapper).batchWrite(pairsCountCaptor.capture(), ignoredCaptor.capture());
//        List<BidRequestCount> receivedPairsCounts = pairsCountCaptor.getValue();
//        assertEquals(1, receivedPairsCounts.size());
//        BidRequestCount receivedPairCount = receivedPairsCounts.get(0);
//        assertEquals(pair.getWh(), receivedPairCount.getWh());
//        assertEquals(1, receivedPairCount.getTypeCounts().size());
//        TypeCount receivedTypeCount = receivedPairCount.getTypeCounts().get(0);
//        assertEquals(pair.getType(), receivedTypeCount.getType());
//        assertEquals(count, receivedTypeCount.getCount());
//    }
//
//    @Test
//    public void GIVEN_persistCalledWithTwoUniqueResources_WHEN_sendQueueToDynamoDB_THEN_refCountsAreOrderedDescending()
//        throws InterruptedException {
//        // Create pairs with different referrers and counts.
//        String resource = "a";
//        Map<BidRequestRec, Long> counts = new HashMap<>();
////        counts.put(new BidRequestRec(resource, "b"), 10L);
////        counts.put(new BidRequestRec(resource, "c"), 7L);
////        counts.put(new BidRequestRec(resource, "d"), 15L);
////        counts.put(new BidRequestRec(resource, "e"), 20L);
////        counts.put(new BidRequestRec(resource, "f"), 20L);
//
//        // Persist the counts
////        persister.persistCounter(counts);
//
//        // Trigger the flush to DynamoDB.
////        persister.sendQueueToDynamoDB(new ArrayList<HttpReferrerPairsCount>());
//
//        // Capture the arguments passed to the mapper when persisting counts to DynamoDB
//        @SuppressWarnings({ "unchecked", "rawtypes" })
//        ArgumentCaptor<List<BidRequestCount>> pairsCountCaptor = ArgumentCaptor.forClass((Class) List.class);
//        @SuppressWarnings({ "unchecked", "rawtypes" })
//        ArgumentCaptor<List<BidRequestCount>> ignoredCaptor = ArgumentCaptor.forClass((Class) List.class);
//        verify(mapper).batchWrite(pairsCountCaptor.capture(), ignoredCaptor.capture());
//
//        List<BidRequestCount> receivedPairsCounts = pairsCountCaptor.getValue();
//        assertEquals(1, receivedPairsCounts.size());
//        BidRequestCount receivedPairCount = receivedPairsCounts.get(0);
//        assertEquals(resource, receivedPairCount.getWh());
//        assertEquals(5, receivedPairCount.getTypeCounts().size());
//
//        // Make sure the counts are descending
//        long lastCount = Long.MAX_VALUE;
//        for (TypeCount refCount : receivedPairCount.getTypeCounts()) {
//            assertTrue("Count did not decrease from the last one seen. " + lastCount + " is not > "
//                    + refCount.getCount(),
//                    lastCount >= refCount.getCount());
//            lastCount = refCount.getCount();
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    @Test
//    public void GIVEN_initializedPersister_WHEN_persist_THEN_countsPersistedInBatch() throws InterruptedException {
//        persister.initialize();
//
//        final BidRequestRec pair = new BidRequestRec();
//
//
//        final long count = 1L;
////        persister.persistCounter(Collections.singletonMap(pair, count));
//
//        // Wait for the persister thread to pick up the new counts
//        Thread.sleep(100);
//
//        // Verify the counts were sent to DynamoDB
//        verify(mapper).batchWrite(Mockito.any(List.class), Mockito.any(List.class));
//    }
//
//    @Test
//    public void GIVEN_initializedPersister_WHEN_checkpoint_THEN_checkpointReturnsSuccessfully()
//        throws InterruptedException {
//        persister.initialize();
//
//        persister.checkpoint();
//    }
}
