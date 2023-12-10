package org.mdholloway.wikitrends;

import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class RevertedRevisionsApp {

    @Inject
    RevertedRevisionStore revertedRevisionStore;

    @Incoming("reverted-revisions")
    public void storeRevertedRevision(Record<Long, RevisionCreate> revisionCreateRecord) {
        revertedRevisionStore.create(revisionCreateRecord.value())
                .subscribe()
                .with(unused -> Log.info("Stored reverted revision: " + revisionCreateRecord.value()));
    }
}
