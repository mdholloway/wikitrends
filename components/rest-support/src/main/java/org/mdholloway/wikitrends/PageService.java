package org.mdholloway.wikitrends;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "wikipedia-rest-api")
@ClientHeaderParam(name = "User-Agent", value = "WikiVibes/1.0.0-SNAPSHOT (michael@mdholloway.org) QuarkusRestClientReactive/3.5.0")
public interface PageService {

    @GET
    @Path("/page/html/{title}/{revision}")
    @Produces(MediaType.TEXT_HTML)
    Uni<String> getPageHtml(@PathParam("title") @Encoded String title,
                            @PathParam("revision") int revision);
}
