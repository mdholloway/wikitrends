package org.mdholloway.wikitrends.eventstreams;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.mdholloway.wikitrends.WikipediaRestService;
import org.mdholloway.wikitrends.eventstreams.model.RecentChangeEvent;

public class MessageProcessor {

    private static final String ENGLISH_WIKIPEDIA = "enwiki";

    @RestClient
    private EventStreamsService eventStreamsService;

    @RestClient
    private WikipediaRestService wikipediaRestService;

    @Channel("revision-titles")
    private Emitter<String> revisionTitleEmitter;

    @Inject
    private ObjectMapper objectMapper;

    public void start() {
        eventStreamsService.streamRecentChangeEvents().subscribe().with(msg -> {
            try {
                RecentChangeEvent recentChange = objectMapper.readValue(msg, RecentChangeEvent.class);
                if (isInterestingRevision(recentChange)) {
                    String title = recentChange.title;
                    int oldRev = recentChange.revision.oldRev.get();
                    int newRev = recentChange.revision.newRev;

                    // TODO: Check for already-fetched previous page HTML in storage
                    Uni<String> getPreviousPageHtml = wikipediaRestService.getPageHtml(title, oldRev);
                    Uni<String> getCurrentPageHtml = wikipediaRestService.getPageHtml(title, newRev);

                    Uni.combine().all().unis(getPreviousPageHtml, getCurrentPageHtml).asTuple()
                            .subscribe().with(responses -> {

                            });
                }
            } catch (JsonProcessingException e) {
                Log.error(e);
            }
        });
    }

    private static boolean isInterestingRevision(RecentChangeEvent recentChange) {
        return !recentChange.bot &&
                recentChange.namespace == 0 &&
                recentChange.revision.oldRev.isPresent() &&
                recentChange.type.equals("edit") &&
                ENGLISH_WIKIPEDIA.equals(recentChange.wiki);
    }
}
