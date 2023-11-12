package org.mdholloway.wikitrends;

import com.fasterxml.jackson.annotation.JsonProperty;

// https://schema.wikimedia.org/repositories/primary/jsonschema/mediawiki/recentchange/1.0.0.yaml
// TODO (someday): Fetch schema(s) and create data class(es) for Jackson dynamically
public class RecentChange {
    public Meta meta;
    public int namespace;
    public String wiki;
    public Revision revision;
    public String type;
    public boolean bot;

    public static class Revision {
        @JsonProperty("new") public long newRev;
        @JsonProperty("old") public long oldRev;
    }

    public static class Meta {
        public String uri;
    }
}
