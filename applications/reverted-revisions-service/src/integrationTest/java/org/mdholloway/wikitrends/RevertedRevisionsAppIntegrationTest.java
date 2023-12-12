package org.mdholloway.wikitrends;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import io.smallrye.reactive.messaging.kafka.Record;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@QuarkusTest
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
public class RevertedRevisionsAppIntegrationTest {

    @InjectMock
    RevertedRevisionStore revertedRevisionStore;

    @Inject
    @Connector("smallrye-in-memory")
    InMemoryConnector connector;

    @Test
    public void itStoresRevertedRevision() {
        InMemorySource<Record<Long, RevisionCreate>> channel = connector.source("reverted-revisions");
        RevisionCreate revisionCreate = buildRevisionCreate();
        Record<Long, RevisionCreate> incoming = Record.of(1L, revisionCreate);

        Uni<Void> storageResult = Uni.createFrom().voidItem();
        UniAssertSubscriber<Void> subscriber = storageResult.subscribe().withSubscriber(UniAssertSubscriber.create());
        when(revertedRevisionStore.create(revisionCreate)).thenReturn(storageResult);

        channel.send(incoming);
        subscriber.awaitItem();
    }

    private static RevisionCreate buildRevisionCreate() {
        RevisionCreate revisionCreate = new RevisionCreate();
        revisionCreate.database = "enwiki";
        revisionCreate.revisionId = 1;
        revisionCreate.pageNamespace = 0;
        revisionCreate.pageId = 1;
        revisionCreate.pageTitle = "Test";
        return revisionCreate;
    }
}
