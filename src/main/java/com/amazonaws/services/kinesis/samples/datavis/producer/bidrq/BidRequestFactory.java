package com.amazonaws.services.kinesis.samples.datavis.producer.bidrq;

import com.amazonaws.services.kinesis.samples.datavis.model.record.BidRequestRec;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by eugennekhai on 24/08/16.
 */
public class BidRequestFactory {
    private List<String> whs;
    private List<String> bidRequestIds;
    private String type;

    /**
     * Create a new generator which will use the resources and referrers provided.
     *
     * @param bidRequestIds List of resources to use when generating a pair.
     */
    public BidRequestFactory(List<String> bidRequestIds, List<String> whs,  String type) {
        if (bidRequestIds == null || bidRequestIds.isEmpty()) {
            throw new IllegalArgumentException("At least 1 resource is required");
        }
        this.bidRequestIds = bidRequestIds;
        this.whs = whs;
        this.type = type;
    }

    /**
     * Creates a new referrer pair using random resources and referrers from the collections provided when this
     * factory was created.
     *
     * @return A new pair with random resource and referrer values.
     */
    public BidRequestRec create() {
        return new BidRequestRec(getRandomId(), type, getRandomWH());
    }

    /**
     * Gets a random resource from the collection of resources.
     *
     * @return A random resource.
     */
    protected String getRandomWH() {
        return whs.get(ThreadLocalRandom.current().nextInt(whs.size()));
    }

    /**
     * Gets a random resource from the collection of bidRequestIds.
     *
     * @return A random resource.
     */
    protected String getRandomId() {
        return bidRequestIds.get(ThreadLocalRandom.current().nextInt(bidRequestIds.size()));
    }

}
