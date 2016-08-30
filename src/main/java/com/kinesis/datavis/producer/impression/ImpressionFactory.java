package com.kinesis.datavis.producer.impression;

import com.kinesis.datavis.model.record.ImpressionRec;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by eugennekhai on 25/08/16.
 */
public class ImpressionFactory {
    private final List<Double> prices;
    private List<String> bidRequestIds;
    private String type;

    /**
     * Create a new generator which will use the bidRequestIds and referrers provided.
     *
     * @param bidRequestIds List of bidRequestIds to use when generating a pair.
     */
    public ImpressionFactory(List<String> bidRequestIds, List<Double> prices, String type) {
        if (bidRequestIds == null || bidRequestIds.isEmpty()) {
            throw new IllegalArgumentException("At least 1 resource is required");
        }
        this.bidRequestIds = bidRequestIds;
        this.prices = prices;

        this.type = type;
    }

    /**
     * Creates a new referrer pair using random bidRequestIds and referrers from the collections provided when this
     * factory was created.
     *
     * @return A new pair with random resource and referrer values.
     */
    public ImpressionRec create() {
        ImpressionRec impressionRec = new ImpressionRec(getRandomId(), type, getRandomPrice());

        return impressionRec;
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
