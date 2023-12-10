package org.mdholloway.wikitrends;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/reverted-revisions")
public class RevertedRevisionsResource {

    @Inject RevertedRevisionStore revertedRevisionStore;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<RevertedRevision>> getRevertedRevisions() {
        return revertedRevisionStore.getAll();
    }
}
