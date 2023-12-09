package org.mdholloway.wikitrends;

import com.fasterxml.jackson.annotation.JsonProperty;

// https://schema.wikimedia.org/repositories/primary/jsonschema/mediawiki/revision/create/2.0.0.yaml
// TODO (someday): Fetch schema(s) and create data class(es) for Jackson dynamically
public class RevisionCreate implements MediaWikiEvent {
    public String database;
    @JsonProperty("page_id") public long pageId;
    @JsonProperty("page_title") public String pageTitle;
    @JsonProperty("page_namespace") public int pageNamespace;
    @JsonProperty("rev_id") public long revisionId;
    public Performer performer;

    @Override
    public int getNamespaceId() {
        return pageNamespace;
    }

    @Override
    public String getWiki() {
        return database;
    }

    @Override
    public String toString() {
        return database + "_" + pageTitle + "_" + revisionId;
    }
}