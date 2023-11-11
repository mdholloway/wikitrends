package org.mdholloway.wikitrends;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "wikipedia-rest-api")
public interface WikipediaRestService {

    @GET
    @Path("/page/html/{title}/{revision}")
    @Produces(MediaType.TEXT_HTML)
    Uni<String> getPageHtml(@PathParam("title") String title,
                            @PathParam("revisionId") int revision);

}
