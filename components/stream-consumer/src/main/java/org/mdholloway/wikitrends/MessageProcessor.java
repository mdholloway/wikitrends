package org.mdholloway.wikitrends;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

public class MessageProcessor {

    private static final String ENGLISH_WIKIPEDIA = "enwiki";

    @RestClient
    private EventStreamsService eventStreamsService;

    @Channel("revision-titles")
    private Emitter<String> revisionTitleEmitter;

    @Inject
    private ObjectMapper objectMapper;

    void start() {
        eventStreamsService.streamRevisionCreateEvents().subscribe().with(msg -> {
            try {
                RevisionCreateEvent event = objectMapper.readValue(msg, RevisionCreateEvent.class);
                if (isInterestingRevision(event)) {
                    revisionTitleEmitter.send(normalize(event.pageTitle));
                }
            } catch (JsonProcessingException e) {
                Log.error(e);
            }
        });
    }

    private static boolean isInterestingRevision(RevisionCreateEvent event) {
        return !event.performer.isBot && event.pageNamespace == 0 && ENGLISH_WIKIPEDIA.equals(event.database);
    }

    private static String normalize(String s) {
        return s.replaceAll("_", " ");
    }
}
