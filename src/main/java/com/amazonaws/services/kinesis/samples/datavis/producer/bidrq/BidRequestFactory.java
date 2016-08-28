package com.amazonaws.services.kinesis.samples.datavis.producer.bidrq;

import com.amazonaws.services.kinesis.samples.datavis.model.record.BidRequestRec;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by eugennekhai on 24/08/16.
 */
public class BidRequestFactory {

    private List<String> resources;
    private String type;

    /**
     * Create a new generator which will use the resources and referrers provided.
     *
     * @param resources List of resources to use when generating a pair.
     */
    public BidRequestFactory(List<String> resources, String type) {
        if (resources == null || resources.isEmpty()) {
            throw new IllegalArgumentException("At least 1 resource is required");
        }
        this.resources = resources;
        this.type = type;
    }

    /**
     * Creates a new referrer pair using random resources and referrers from the collections provided when this
     * factory was created.
     *
     * @return A new pair with random resource and referrer values.
     */
    public BidRequestRec create() {
        String wh = getRandomWH();

        return new BidRequestRec("", type, wh);
    }

    /**
     * Gets a random resource from the collection of resources.
     *
     * @return A random resource.
     */
    protected String getRandomWH() {
        return resources.get(ThreadLocalRandom.current().nextInt(resources.size()));
    }


}
