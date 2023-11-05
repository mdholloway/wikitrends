package org.mdholloway.wikitrends;

import com.fasterxml.jackson.annotation.JsonProperty;

// https://schema.wikimedia.org/repositories/primary/jsonschema/mediawiki/revision/create/2.0.0.yaml
// TODO (someday): Fetch schema(s) and programmatically create data class(es) for Jackson
public class RevisionCreateEvent {
    public String database;
    @JsonProperty("page_title") public String pageTitle;
    @JsonProperty("page_namespace") public int pageNamespace;
    public Performer performer;

    public static class Performer {
        @JsonProperty("user_is_bot") boolean isBot;
    }
}
