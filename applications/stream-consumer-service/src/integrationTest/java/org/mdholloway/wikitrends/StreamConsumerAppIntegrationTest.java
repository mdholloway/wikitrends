package org.mdholloway.wikitrends;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.kafka.Record;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
public class StreamConsumerAppIntegrationTest {

    @Inject
    @Connector("smallrye-in-memory")
    InMemoryConnector connector;

    @Test
    public void itProducesValidRevisionCreate() {
        InMemorySink<Record<Long, RevisionCreate>> revisionCreates = connector.sink("article-revision-creates");
        await().<List<? extends Message<Record<Long, RevisionCreate>>>>until(revisionCreates::received, messages -> messages.size() == 1);

        Record<Long, RevisionCreate> produced = revisionCreates.received().get(0).getPayload();
        long key = produced.key();
        RevisionCreate revisionCreate = produced.value();

        assertEquals(key, 1); // rev_id
        assertEquals(revisionCreate.database, "enwiki");
        assertEquals(revisionCreate.revisionId, 1);
        assertEquals(revisionCreate.pageId, 1);
        assertEquals(revisionCreate.pageNamespace, 0);
        assertEquals(revisionCreate.pageTitle, "test");
    }

    @Test
    public void itProducesValidTagsChange() {
        InMemorySink<Record<Long, TagsChange>> tagsChanges = connector.sink("article-revision-tags-changes");
        await().<List<? extends Message<Record<Long, TagsChange>>>>until(tagsChanges::received, messages -> messages.size() == 1);

        Record<Long, TagsChange> produced = tagsChanges.received().get(0).getPayload();
        long key = produced.key();
        TagsChange tagsChange = produced.value();

        assertEquals(key, 1); // rev_id
        assertEquals(tagsChange.database, "enwiki");
        assertEquals(tagsChange.revisionId, 1);
        assertEquals(tagsChange.pageId, 1);
        assertEquals(tagsChange.pageNamespace, 0);
        assertEquals(tagsChange.pageTitle, "test");
        assertArrayEquals(tagsChange.tags, new String[] { "mw-reverted" });
        assertArrayEquals(tagsChange.priorState.tags, new String[0]);
    }
}
