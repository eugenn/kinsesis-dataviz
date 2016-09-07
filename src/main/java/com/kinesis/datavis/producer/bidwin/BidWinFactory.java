package com.kinesis.datavis.producer.bidwin;

import com.kinesis.datavis.model.record.BidWinRec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by eugennekhai on 25/08/16.
 */
public class BidWinFactory {
    private final List<Double> prices;
    private List<String> bidRequestIds;
    private Map<String, String> mapping = new HashMap<>();

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

//        mapping.put("b05361fe-4413-4a0e-92ae-cc2d9314ba16", "female");
//        mapping.put("e268b3fa-d53b-4f73-bb2f-31f429831e4a", "male");
//        mapping.put("fcc5f84b-c004-43c8-b25b-a2548614dff9", "male");
//        mapping.put("9c9af2a7-15f7-4cd2-b810-444d54599881", "male");
//        mapping.put("1673d571-9298-4d52-b586-099dd46488e3", "male");
    }

    /**
     * Creates a new referrer pair using random bidRequestIds and referrers from the collections provided when this
     * factory was created.
     *
     * @return A new pair with random resource and referrer values.
     */
    public BidWinRec create() {
        String id = getRandomId();
        BidWinRec winRec = new BidWinRec(id, "undefined", mapping.get(id), getRandomPrice());

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
