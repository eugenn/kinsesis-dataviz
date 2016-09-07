package com.kinesis.datavis.producer.bidwin;

import com.kinesis.datavis.model.record.BidWinRec;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by eugennekhai on 25/08/16.
 */
public class BidWinFactory {
    private final List<Double> prices;
    private List<String> bidRequestIds;

    /**
     * Create a new generator which will use the bidRequestIds and referrers provided.
     *
     * @param bidRequestIds List of bidRequestIds to use when generating a pair.
     */
    public BidWinFactory(List<String> bidRequestIds, List<Double> prices) {
        if (bidRequestIds == null || bidRequestIds.isEmpty()) {
            throw new IllegalArgumentException("At least 1 resource is required");
        }
        this.bidRequestIds = bidRequestIds;
        this.prices = prices;

    }

    /**
     * Creates a new referrer pair using random bidRequestIds and referrers from the collections provided when this
     * factory was created.
     *
     * @return A new pair with random resource and referrer values.
     */
    public BidWinRec create() {
        String id = getRandomId();
        BidWinRec winRec = new BidWinRec(getRandomId(), "undefined", "undefined", getRandomPrice());

        return winRec;
    }

    /**
     * Gets a random resource from the collection of bidRequestIds.
     *
     * @return A random resource.
     */
    protected String getRandomId() {
        return bidRequestIds.get(ThreadLocalRandom.current().nextInt(bidRequestIds.size()));
    }

    /**
     * Gets a random referrer from the collection of referrers.
     *
     * @return A random referrer.
     */
    protected Double getRandomPrice() {
        return prices.get(ThreadLocalRandom.current().nextInt(prices.size()));
    }


}
