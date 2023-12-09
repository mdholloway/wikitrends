package org.mdholloway.wikitrends;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

// https://schema.wikimedia.org/repositories/primary/jsonschema/mediawiki/revision/tags-change/1.0.0.yaml
// TODO (someday): Fetch schema(s) and create data class(es) for Jackson dynamically
public class TagsChange implements MediaWikiEvent {
    public String database;
    @JsonProperty("page_id") public long pageId;
    @JsonProperty("page_title") public String pageTitle;
    @JsonProperty("page_namespace") public int pageNamespace;
    @JsonProperty("rev_id") public long revisionId;
    public Performer performer;
    public String[] tags;
    @JsonProperty("prior_state") public PriorState priorState;
    public Meta meta;

    @Override
    public int getNamespaceId() {
        return pageNamespace;
    }

    @Override
    public String getWiki() {
        return database;
    }

    public static class PriorState {
        public String[] tags;
    }

    public static class Meta {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        public Instant dt;
    }
}
