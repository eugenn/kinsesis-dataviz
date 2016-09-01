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

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Regs {
    private final Integer coppa;
    private final Ext ext;

    public Regs(
            @JsonProperty("coppa") Integer coppa,
            @JsonProperty("ext") Ext ext
    ) {
        this.coppa = coppa;
        this.ext = ext;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty
    public Integer getCoppa() {
        return coppa;
    }

    @JsonProperty
    public Ext getExt() {
        return ext;
    }

    public static class Builder {
        private Integer coppa;
        private Ext ext;

        public Builder() {
        }

        public Builder coppa(final Integer coppa) {
            this.coppa = coppa;
            return this;
        }

        public Builder ext(final Ext ext) {
            this.ext = ext;
            return this;
        }

        public Regs build() {
            return new Regs(coppa, ext);
        }
    }
}
