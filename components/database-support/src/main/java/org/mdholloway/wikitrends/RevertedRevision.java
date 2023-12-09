package org.mdholloway.wikitrends;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class RevertedRevision {

    private long id;
    private String database;
    private long pageId;
    private String pageTitle;
    private int pageNamespace;
    private Instant createdAt;
    private Instant revertedAt;

    public static RevertedRevision from(RevisionCreate revisionCreate, TagsChange tagsChange) {
        return new RevertedRevision.Builder()
                .setId(revisionCreate.revisionId)
                .setDatabase(revisionCreate.database)
                .setPageId(revisionCreate.pageId)
                .setPageTitle(revisionCreate.pageTitle)
                .setPageNamespace(revisionCreate.pageNamespace)
                .setCreatedAt(revisionCreate.revisionTimestamp)
                .setRevertedAt(tagsChange.meta.dt)
                .build();
    }

    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
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

    public int getPageNamespace() {
        return pageNamespace;
    }

    public void setPageNamespace(int pageNamespace) {
        this.pageNamespace = pageNamespace;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getRevertedAt() {
        return revertedAt;
    }

    public void setRevertedAt(Instant revertedAt) {
        this.revertedAt = revertedAt;
    }

    public static class Builder {
        private long id;
        private String database;
        private long pageId;
        private String pageTitle;
        private int pageNamespace;
        private Instant createdAt;
        private Instant revertedAt;

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setDatabase(String database) {
            this.database = database;
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

        public Builder setPageNamespace(int pageNamespace) {
            this.pageNamespace = pageNamespace;
            return this;
        }

        public Builder setCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setRevertedAt(Instant revertedAt) {
            this.revertedAt = revertedAt;
            return this;
        }

        public RevertedRevision build() {
            return new RevertedRevision(id, database, pageId, pageTitle, pageNamespace, createdAt, revertedAt);
        }
    }

    private RevertedRevision(long id, String database, long pageId, String pageTitle, int pageNamespace, Instant createdAt, Instant revertedAt) {
        this.id = id;
        this.database = database;
        this.pageId = pageId;
        this.pageTitle = pageTitle;
        this.pageNamespace = pageNamespace;
        this.createdAt = createdAt;
        this.revertedAt = revertedAt;
    }
}
