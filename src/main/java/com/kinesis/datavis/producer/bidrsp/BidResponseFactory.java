package com.kinesis.datavis.producer.bidrsp;

import com.kinesis.openrtb.BidResponse;
import com.kinesis.openrtb.Ext;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by eugennekhai on 25/08/16.
 */
@AllArgsConstructor
public class BidResponseFactory {
    private List<String> bidRqs;
    private List<String> bannerIds;
    private List<String> audienceIds;
    AtomicInteger hackDigit;

    public BidResponse create() {
        Ext ext = Ext.builder().put("uniq_id", getRandomBannerId()).put("audience_id", getRandomAudienceIds()).build();
        BidResponse winRec = BidResponse.builder().id(UUID.randomUUID().toString()).ext(ext).build();
        hackDigit = new AtomicInteger(0);
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
