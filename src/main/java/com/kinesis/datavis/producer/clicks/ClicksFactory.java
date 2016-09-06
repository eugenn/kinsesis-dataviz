package com.kinesis.datavis.producer.clicks;

import com.kinesis.datavis.model.record.ClicksRec;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by eugennekhai on 25/08/16.
 */
public class ClicksFactory {
    private List<String> bidRequestIds;

    /**
     * Create a new generator which will use the bidRequestIds and referrers provided.
     *
     * @param bidRequestIds List of bidRequestIds to use when generating a pair.
     */
    public ClicksFactory(List<String> bidRequestIds) {
        if (bidRequestIds == null || bidRequestIds.isEmpty()) {
            throw new IllegalArgumentException("At least 1 resource is required");
        }
        this.bidRequestIds = bidRequestIds;

    }

    /**
     * Creates a new referrer pair using random bidRequestIds and referrers from the collections provided when this
     * factory was created.
     *
     * @return A new pair with random resource and referrer values.
     */
    public ClicksRec create() {
        ClicksRec clicksRec = new ClicksRec(getRandomId(), "undefined", "undefined");

        return clicksRec;
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
