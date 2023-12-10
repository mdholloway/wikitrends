package org.mdholloway.wikitrends;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class RevertedRevision extends PanacheEntity {

    private String wiki;
    private long pageId;
    private String pageTitle;
    private long revisionId;

    public static RevertedRevision from(RevisionCreate revisionCreate) {
        return new RevertedRevision.Builder()
                .setWiki(revisionCreate.database)
                .setPageId(revisionCreate.pageId)
                .setPageTitle(revisionCreate.pageTitle)
                .setRevisionId(revisionCreate.revisionId)
                .build();
    }

    public RevertedRevision() {}

    public String getWiki() {
        return wiki;
    }

    public void setWiki(String wiki) {
        this.wiki = wiki;
    }

    public long getPageId() {
        return pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public long getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(long revisionId) {
        this.revisionId = revisionId;
    }

    public static class Builder {
        private String wiki;
        private long pageId;
        private String pageTitle;
        private long revisionId;

        public Builder setWiki(String wiki) {
            this.wiki = wiki;
            return this;
        }

        public Builder setPageId(long pageId) {
            this.pageId = pageId;
            return this;
        }

        public Builder setPageTitle(String pageTitle) {
            this.pageTitle = pageTitle;
            return this;
        }

        public Builder setRevisionId(long revisionId) {
            this.revisionId = revisionId;
            return this;
        }

        public RevertedRevision build() {
            return new RevertedRevision(wiki, pageId, pageTitle, revisionId);
        }
    }

    private RevertedRevision(String wiki, long pageId, String pageTitle, long revisionId) {
        this.wiki = wiki;
        this.pageId = pageId;
        this.pageTitle = pageTitle;
        this.revisionId = revisionId;
    }
}
