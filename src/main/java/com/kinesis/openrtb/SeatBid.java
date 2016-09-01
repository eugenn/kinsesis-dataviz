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

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SeatBid {
    private final String seat;
    private final List<Bid> bid;
    private final Integer group;
    private final Ext ext;

    public SeatBid(
            @JsonProperty("seat") String seat,
            @JsonProperty("bid") List<Bid> bid,
            @JsonProperty("group") Integer group,
            @JsonProperty("ext") Ext ext
    ) {
        this.seat = seat;
        this.bid = bid;
        this.group = group;
        this.ext = ext;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty
    public String getSeat() {
        return seat;
    }

    @JsonProperty
    public List<Bid> getBid() {
        return bid;
    }

    @JsonProperty
    public Integer getGroup() {
        return group;
    }

    @JsonProperty
    public Ext getExt() {
        return ext;
    }

    public static class Builder {
        private String seat;
        private List<Bid> bid;
        private Integer group;
        private Ext ext;

        public Builder() {
        }

        public Builder seat(final String seat) {
            this.seat = seat;
            return this;
        }

        public Builder bid(final List<Bid> bid) {
            this.bid = bid;
            return this;
        }

        public Builder group(final Integer group) {
            this.group = group;
            return this;
        }

        public Builder ext(final Ext ext) {
            this.ext = ext;
            return this;
        }

        public SeatBid build() {
            return new SeatBid(seat, bid, group, ext);
        }
    }
}
