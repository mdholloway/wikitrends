package org.mdholloway.wikitrends;

import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/stream")
@RegisterRestClient(configKey = "event-streams")
public interface EventStreamsService {

    @GET
    @Path("/revision-create")
    @Produces("text/event-stream")
    Multi<String> streamRevisionCreateEvents();
}
