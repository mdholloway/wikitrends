package org.mdholloway.wikitrends;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.arc.Arc;
import io.quarkus.arc.InstanceHandle;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@QuarkusMain
public class StreamConsumer implements QuarkusApplication {

    private static final String ENGLISH_WIKIPEDIA = "enwiki";

    @RestClient
    EventStreamsService eventStreamsService;

    public static void main(String... args) {
        Quarkus.run(StreamConsumer.class, args);
    }

    @Override
    public int run(String... args) {
        eventStreamsService.streamRevisionCreateEvents().subscribe().with(msg -> {
            // TODO: Get deserialization off the wire working with a custom deserializer?
            try (InstanceHandle<ObjectMapper> objectMapper = Arc.container().instance(ObjectMapper.class)) {
                RevisionCreateEvent event = objectMapper.get().readValue(msg, RevisionCreateEvent.class);
                if (event.pageNamespace == 0 && ENGLISH_WIKIPEDIA.equals(event.database)) {
                    Log.debug(normalize(event.pageTitle));
                }
            } catch (JsonProcessingException e) {
                Log.error(e);
            }
        });

        Quarkus.waitForExit();
        return 0;
    }

    private static String normalize(String s) {
        return s.replaceAll("_", " ");
    }
}
