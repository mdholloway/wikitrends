package org.mdholloway.wikitrends;

import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "wikimedia-event-streams")
@ClientHeaderParam(name = "User-Agent", value = "WikiVibes/1.0.0-SNAPSHOT (michael@mdholloway.org) QuarkusRestClientReactive/3.5.0")
public interface EventStreamsService {

    @GET
    @Path("/mediawiki.revision-create")
    Multi<String> streamRevisionCreates();

    @GET
    @Path("/mediawiki.revision-tags-change")
    Multi<String> streamTagsChanges();
}
