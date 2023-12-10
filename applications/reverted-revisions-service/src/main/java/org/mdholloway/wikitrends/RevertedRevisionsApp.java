package org.mdholloway.wikitrends;

import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.util.List;

@ApplicationScoped
public class RevertedRevisionsApp {

    @Inject
    RevertedRevisionStore revertedRevisionStore;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<RevertedRevision> getRevertedRevisions() {
        return RevertedRevision.listAll();
    }

    @Incoming("reverted-revisions")
    public void storeRevertedRevision(Record<Long, RevisionCreate> revisionCreateRecord) {
        revertedRevisionStore.create(revisionCreateRecord.value())
                .subscribe()
                .with(unused -> Log.info("Stored reverted revision: " + revisionCreateRecord.value()));
    }
}
