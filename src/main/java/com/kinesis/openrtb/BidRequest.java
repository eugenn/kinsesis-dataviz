/*
 * Rad-tech-datatypes.
 * Copyright 2014 Metamarkets Group Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kinesis.openrtb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@lombok.Data
public class BidRequest {
    private final String requestId;
    private final List<Imp> impressions;
    private final Site site;
    private final App app;
    private final Device device;
    private final User user;
    private final Integer test;
    private final Integer auctionType;
    private final Integer tmax;
    private final List<String> wseat;
    private final Integer allimps;
    private final List<String> cur;
    private final List<String> bcat;
    private final List<String> badv;
    private final Regs regs;
    private final Ext ext;
    private String bannerId;
    private String audienceId;

    public BidRequest(
            @JsonProperty("id") String requestId,
            @JsonProperty("imp") List<Imp> impressions,
            @JsonProperty("site") Site site,
            @JsonProperty("app") App app,
            @JsonProperty("device") Device device,
            @JsonProperty("user") User user,
            @JsonProperty("test") Integer test,
            @JsonProperty("at") Integer auctionType,
            @JsonProperty("tmax") Integer tmax,
            @JsonProperty("wseat") List<String> wseat,
            @JsonProperty("allimps") Integer allimps,
            @JsonProperty("cur") List<String> cur,
            @JsonProperty("bcat") List<String> bcat,
            @JsonProperty("badv") List<String> badv,
            @JsonProperty("regs") Regs regs,
            @JsonProperty("ext") Ext ext
    ) {
        this.requestId = requestId;
        this.impressions = impressions;
        this.site = site;
        this.app = app;
        this.device = device;
        this.user = user;
        this.test = test;
        this.auctionType = auctionType;
        this.tmax = tmax;
        this.wseat = wseat;
        this.allimps = allimps;
        this.cur = cur;
        this.bcat = bcat;
        this.badv = badv;
        this.regs = regs;
        this.ext = ext;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty("id")
    public String getRequestId() {
        return requestId;
    }

    @JsonProperty("imp")
    public List<Imp> getImpressions() {
        return impressions;
    }

    @JsonProperty
    public Site getSite() {
        return site;
    }

    @JsonProperty
    public App getApp() {
        return app;
    }

    @JsonProperty
    public Device getDevice() {
        return device;
    }

    @JsonProperty
    public Integer getTest() {
        return test;
    }

    @JsonProperty
    public User getUser() {
        return user;
    }

    @JsonProperty("at")
    public Integer getAuctionType() {
        return auctionType;
    }

    @JsonProperty
    public Integer getTmax() {
        return tmax;
    }

    @JsonProperty
    public List<String> getWseat() {
        return wseat;
    }

    @JsonProperty
    public Integer getAllimps() {
        return allimps;
    }

    @JsonProperty
    public List<String> getCur() {
        return cur;
    }

    @JsonProperty
    public List<String> getBadv() {
        return badv;
    }

    @JsonProperty
    public List<String> getBcat() {
        return bcat;
    }

    @JsonProperty
    public Regs getRegs() {
        return regs;
    }

    @JsonProperty
    public Ext getExt() {
        return ext;
    }

    public static class Builder {
        private String requestId;
        private List<Imp> impressions;
        private Site site;
        private App app;
        private Device device;
        private User user;
        private Integer test;
        private Integer auctionType;
        private Integer tmax;
        private List<String> wseat;
        private Integer allimps;
        private List<String> cur;
        private List<String> bcat;
        private List<String> badv;
        private Regs regs;
        private Ext ext;

        public Builder() {
        }

        public Builder requestId(final String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder impressions(final List<Imp> impressions) {
            this.impressions = impressions;
            return this;
        }

        public Builder app(final App app) {
            this.app = app;
            return this;
        }

        public Builder site(final Site site) {
            this.site = site;
            return this;
        }

        public Builder device(final Device device) {
            this.device = device;
            return this;
        }

        public Builder user(final User user) {
            this.user = user;
            return this;
        }

        public Builder test(final Integer test) {
            this.test = test;
            return this;
        }

        public Builder auctionType(final Integer auctionType) {
            this.auctionType = auctionType;
            return this;
        }

        public Builder tmax(final Integer tmax) {
            this.tmax = tmax;
            return this;
        }

        public Builder wseat(final List<String> wseat) {
            this.wseat = wseat;
            return this;
        }

        public Builder allimps(final Integer allimps) {
            this.allimps = allimps;
            return this;
        }

        public Builder cur(final List<String> cur) {
            this.cur = cur;
            return this;
        }

        public Builder bcat(final List<String> bcat) {
            this.bcat = bcat;
            return this;
        }

        public Builder badv(final List<String> badv) {
            this.badv = badv;
            return this;
        }

        public Builder regs(final Regs regs) {
            this.regs = regs;
            return this;
        }

        public Builder ext(final Ext ext) {
            this.ext = ext;
            return this;
        }

        public BidRequest build() {
            return new BidRequest(
                    requestId,
                    impressions,
                    site,
                    app,
                    device,
                    user,
                    test,
                    auctionType,
                    tmax,
                    wseat,
                    allimps,
                    cur,
                    bcat,
                    badv,
                    regs,
                    ext
            );
        }
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        BidRequest that = (BidRequest) o;
//
//        if (requestId != null ? !requestId.equals(that.requestId) : that.requestId != null) return false;
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = requestId != null ? requestId.hashCode() : 0;
//        return result;
//    }

}
