package org.mdholloway.wikitrends;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
}
