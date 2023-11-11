package org.mdholloway.wikitrends.eventstreams;

import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "wikimedia-event-streams")
public interface EventStreamsService {

    @GET
    @Path("/revision-create")
    @Produces("text/event-stream")
    Multi<String> streamRevisionCreateEvents();

    @GET
    @Path("/recentchange")
    @Produces("text/event-stream")
    Multi<String> streamRecentChangeEvents();
}
