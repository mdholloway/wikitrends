package org.mdholloway.wikitrends;

public class StoredRevisions {
    public String oldKey;
    public String newKey;

    public static StoredRevisions with(String oldKey, String newKey) {
        return new StoredRevisions(oldKey, newKey);
    }

    private StoredRevisions(String oldKey, String newKey) {
        this.newKey = newKey;
        this.oldKey = oldKey;
    }
}
