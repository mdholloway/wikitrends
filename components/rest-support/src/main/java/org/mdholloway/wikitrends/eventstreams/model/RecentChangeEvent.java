package org.mdholloway.wikitrends.eventstreams.model;

import com.fasterxml.jackson.annotation.JsonProperty;

// https://schema.wikimedia.org/repositories/primary/jsonschema/mediawiki/recentchange/1.0.0.yaml
// TODO (someday): Fetch schema(s) and create data class(es) for Jackson dynamically
public class RecentChangeEvent {
    public Meta meta;
    public int namespace;
    public String wiki;
    public String title;
    public Revision revision;
    public String type; // TODO: Enum
    public boolean bot;
    public boolean minor;

    public static class Revision {
        @JsonProperty("new") public long newRev;
        @JsonProperty("old") public long oldRev;
    }

    public static class Meta {
        public String uri;
    }
}
