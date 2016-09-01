package com.kinesis.datavis.producer.bidrq;

import com.kinesis.openrtb.BidRequest;
import com.kinesis.openrtb.Device;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by eugennekhai on 24/08/16.
 */
public class BidRequestFactory {
    private List<String> whs;
    private List<String> bidRequestIds;

    /**
     * Create a new generator which will use the resources and referrers provided.
     *
     * @param bidRequestIds List of resources to use when generating a pair.
     */
    public BidRequestFactory(List<String> bidRequestIds, List<String> whs) {
        if (bidRequestIds == null || bidRequestIds.isEmpty()) {
            throw new IllegalArgumentException("At least 1 resource is required");
        }
        this.bidRequestIds = bidRequestIds;
        this.whs = whs;
    }

    /**
     * Creates a new referrer pair using random resources and referrers from the collections provided when this
     * factory was created.
     *
     * @return A new pair with random resource and referrer values.
     */
    public BidRequest create() {
        Device device = Device.builder().width(300).height(200).build();
        BidRequest bidRequest = BidRequest.builder().requestId(UUID.randomUUID().toString()).device(device).build();

        return bidRequest;
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
