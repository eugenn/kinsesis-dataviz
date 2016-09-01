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
public class Content {
    private final String id;
    private final Integer episode;
    private final String title;
    private final String series;
    private final String season;
    private final String url;
    private final List<String> cat;
    private final Integer videoQuality;
    private final String keywords;
    private final String contentRating;
    private final String userRating;
    private final String context;
    private final Integer liveStream;
    private final Integer sourceRelationship;
    private final Producer producer;
    private final Integer len;
    private final Integer qagMediaRating;
    private final Integer embeddable;
    private final String language;
    private final Ext ext;

    public Content(
            @JsonProperty("id") String id,
            @JsonProperty("episode") Integer episode,
            @JsonProperty("title") String title,
            @JsonProperty("series") String series,
            @JsonProperty("season") String season,
            @JsonProperty("url") String url,
            @JsonProperty("cat") List<String> cat,
            @JsonProperty("videoquality") Integer videoQuality,
            @JsonProperty("keywords") String keywords,
            @JsonProperty("contentrating") String contentRating,
            @JsonProperty("userrating") String userRating,
            @JsonProperty("context") String context,
            @JsonProperty("livestream") Integer liveStream,
            @JsonProperty("sourcerelationship") Integer sourceRelationship,
            @JsonProperty("producer") Producer producer,
            @JsonProperty("len") Integer len,
            @JsonProperty("qagmediarating") Integer qagMediaRating,
            @JsonProperty("embeddable") Integer embeddable,
            @JsonProperty("language") String language,
            @JsonProperty("ext") Ext ext
    ) {
        this.id = id;
        this.episode = episode;
        this.title = title;
        this.series = series;
        this.season = season;
        this.url = url;
        this.cat = cat;
        this.videoQuality = videoQuality;
        this.keywords = keywords;
        this.contentRating = contentRating;
        this.userRating = userRating;
        this.context = context;
        this.liveStream = liveStream;
        this.sourceRelationship = sourceRelationship;
        this.producer = producer;
        this.len = len;
        this.qagMediaRating = qagMediaRating;
        this.embeddable = embeddable;
        this.language = language;
        this.ext = ext;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public Integer getEpisode() {
        return episode;
    }

    @JsonProperty
    public String getTitle() {
        return title;
    }

    @JsonProperty
    public String getSeries() {
        return series;
    }

    @JsonProperty
    public String getSeason() {
        return season;
    }

    @JsonProperty
    public String getUrl() {
        return url;
    }

    @JsonProperty
    public List<String> getCat() {
        return cat;
    }

    @JsonProperty("videoquality")
    public Integer getVideoQuality() {
        return videoQuality;
    }

    @JsonProperty
    public String getKeywords() {
        return keywords;
    }

    @JsonProperty("contentrating")
    public String getContentRating() {
        return contentRating;
    }

    @JsonProperty("userrating")
    public String getUserRating() {
        return userRating;
    }

    @JsonProperty
    public String getContext() {
        return context;
    }

    @JsonProperty("livestream")
    public Integer getLiveStream() {
        return liveStream;
    }

    @JsonProperty("sourcerelationship")
    public Integer getSourceRelationship() {
        return sourceRelationship;
    }

    @JsonProperty
    public Producer getProducer() {
        return producer;
    }

    @JsonProperty
    public Integer getLen() {
        return len;
    }

    @JsonProperty("qagmediarating")
    public Integer getQagMediaRating() {
        return qagMediaRating;
    }

    @JsonProperty("embeddable")
    public Integer getEmbeddable() {
        return embeddable;
    }

    @JsonProperty
    public String getLanguage() {
        return language;
    }

    @JsonProperty
    public Ext getExt() {
        return ext;
    }

    public static class Builder {
        private String id;
        private Integer episode;
        private String title;
        private String series;
        private String season;
        private String url;
        private List<String> cat;
        private Integer videoQuality;
        private String keywords;
        private String contentRating;
        private String userRating;
        private String context;
        private Integer liveStream;
        private Integer sourceRelationship;
        private Producer producer;
        private Integer len;
        private Integer qagMediaRating;
        private Integer embeddable;
        private String language;
        private Ext ext;

        public Builder() {
        }

        public Builder id(final String id) {
            this.id = id;
            return this;
        }

        public Builder episode(final Integer episode) {
            this.episode = episode;
            return this;
        }

        public Builder title(final String title) {
            this.title = title;
            return this;
        }

        public Builder series(final String series) {
            this.series = series;
            return this;
        }

        public Builder season(final String season) {
            this.season = season;
            return this;
        }

        public Builder url(final String url) {
            this.url = url;
            return this;
        }

        public Builder cat(final List<String> cat) {
            this.cat = cat;
            return this;
        }

        public Builder videoQuality(final Integer videoQuality) {
            this.videoQuality = videoQuality;
            return this;
        }

        public Builder keywords(final String keywords) {
            this.keywords = keywords;
            return this;
        }

        public Builder contentRating(final String contentRating) {
            this.contentRating = contentRating;
            return this;
        }

        public Builder userRating(final String userRating) {
            this.userRating = userRating;
            return this;
        }

        public Builder context(final String context) {
            this.context = context;
            return this;
        }

        public Builder liveStream(final Integer liveStream) {
            this.liveStream = liveStream;
            return this;
        }

        public Builder sourceRelationship(final Integer sourceRelationship) {
            this.sourceRelationship = sourceRelationship;
            return this;
        }

        public Builder producer(final Producer producer) {
            this.producer = producer;
            return this;
        }

        public Builder len(final Integer len) {
            this.len = len;
            return this;
        }

        public Builder qagMediaRating(final Integer qagMediaRating) {
            this.qagMediaRating = qagMediaRating;
            return this;
        }

        public Builder embeddable(final Integer embeddable) {
            this.embeddable = embeddable;
            return this;
        }

        public Builder language(final String language) {
            this.language = language;
            return this;
        }

        public Builder ext(final Ext ext) {
            this.ext = ext;
            return this;
        }

        public Content build() {
            return new Content(
                    id,
                    episode,
                    title,
                    series,
                    season,
                    url,
                    cat,
                    videoQuality,
                    keywords,
                    contentRating,
                    userRating,
                    context,
                    liveStream,
                    sourceRelationship,
                    producer,
                    len,
                    qagMediaRating,
                    embeddable,
                    language,
                    ext
            );
        }
    }
}
