package com.kinesis.datavis.producer.bidrsp;

import com.kinesis.datavis.model.record.BidResponseRec;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by eugennekhai on 25/08/16.
 */
public class BidResponseFactory {
    private List<String> bannerIds;
    private String type;

    /**
     * Create a new generator which will use the bidRequestIds and referrers provided.
     *
     * @param bannerIds List of bidRequestIds to use when generating a pair.
     */
    public BidResponseFactory(List<String> bannerIds, String type) {
        if (bannerIds == null || bannerIds.isEmpty()) {
            throw new IllegalArgumentException("At least 1 resource is required");
        }
        this.bannerIds = bannerIds;

        this.type = type;
    }

    /**
     * Creates a new referrer pair using random bidRequestIds and referrers from the collections provided when this
     * factory was created.
     *
     * @return A new pair with random resource and referrer values.
     */
    public BidResponseRec create() {
        BidResponseRec winRec = new BidResponseRec(getRandomId(), type);

        return winRec;
    }

    /**
     * Gets a random resource from the collection of bidRequestIds.
     *
     * @return A random resource.
     */
    protected String getRandomId() {
        return bannerIds.get(ThreadLocalRandom.current().nextInt(bannerIds.size()));
    }

    /**
     * Gets a random referrer from the collection of referrers.
     *
     * @return A random referrer.
     */
//    protected BigDecimal getRandomPrice() {
//        return prices.get(ThreadLocalRandom.current().nextInt(prices.size()));
//    }
}
