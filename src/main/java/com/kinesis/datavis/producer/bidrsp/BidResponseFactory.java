package com.kinesis.datavis.producer.bidrsp;

import com.kinesis.openrtb.BidResponse;
import com.kinesis.openrtb.Ext;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by eugennekhai on 25/08/16.
 */
@AllArgsConstructor
public class BidResponseFactory {
    private List<String> bidRqs;
    private List<String> bannerIds;
    private List<String> audienceIds;

    public BidResponse create() {
        Ext ext = Ext.builder().put("uniq_id", getRandomBannerId()).put("audience_id", getRandomAudienceIds()).build();
        BidResponse winRec = BidResponse.builder().id(getRandomId()).ext(ext).build();

        return winRec;
    }

    protected String getRandomBannerId() {
        return bannerIds.get(ThreadLocalRandom.current().nextInt(bannerIds.size()));
    }

    protected String getRandomId() {
        return bidRqs.get(ThreadLocalRandom.current().nextInt(bidRqs.size()));
    }

    protected String getRandomAudienceIds() {
        return audienceIds.get(ThreadLocalRandom.current().nextInt(audienceIds.size()));
    }
}
