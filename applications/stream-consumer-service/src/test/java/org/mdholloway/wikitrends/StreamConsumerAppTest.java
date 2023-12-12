package org.mdholloway.wikitrends;

import io.quarkus.test.InjectMock;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mdholloway.wikitrends.StreamConsumerApp.isArticle;
import static org.mdholloway.wikitrends.StreamConsumerApp.parse;
import static org.mdholloway.wikitrends.StreamConsumerApp.tagWasAdded;

public class StreamConsumerAppTest {

    @Test
    public void parse_parsesValidObject() {
        RevisionCreate expected = new RevisionCreate();
        expected.revisionId = 1;

        String message = "{ \"rev_id\": 1 }";
        assertEquals(parse(message, RevisionCreate.class), Optional.of(expected));
    }

    @Test
    public void parse_returnsOptionalEmptyForInvalidObject() {
        String message = "{ \"rev_id\": Ω }";
        assertEquals(parse(message, RevisionCreate.class), Optional.empty());
    }

    @Test
    public void isArticle_returnsTrueForNamespaceZero() {
        RevisionCreate revisionCreate = new RevisionCreate();
        revisionCreate.pageNamespace = 0;
        assertTrue(isArticle(revisionCreate));
    }

    @Test
    public void isArticle_returnsFalseForNamespaceNotZero() {
        RevisionCreate revisionCreate = new RevisionCreate();
        revisionCreate.pageNamespace = 1;
        assertFalse(isArticle(revisionCreate));
    }

    @Test
    public void tagWasAdded_returnsTrueWhenTagWasAdded() {
        TagsChange tagsChange = buildTagsChange(new String[0], new String[] { "test" });
        assertTrue(tagWasAdded(tagsChange, "test"));
    }

    @Test
    public void tagWasAdded_returnsFalseWhenDifferentTagWasAdded() {
        TagsChange tagsChange = buildTagsChange(new String[0], new String[] { "foo" });
        assertFalse(tagWasAdded(tagsChange, "test"));
    }

    @Test
    public void tagWasAdded_returnsFalseWhenTagWasAlreadyPresent() {
        TagsChange tagsChange = buildTagsChange(new String[] { "test" }, new String[] { "test" });
        assertFalse(tagWasAdded(tagsChange, "test"));
    }

    private static TagsChange buildTagsChange(String[] before, String[] after) {
        TagsChange tagsChange = new TagsChange();
        tagsChange.tags = after;
        tagsChange.priorState = new TagsChange.PriorState();
        tagsChange.priorState.tags = before;
        return tagsChange;
    }
}
