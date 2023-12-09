package org.mdholloway.wikitrends;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class StreamConsumerApp {

    @RestClient
    private EventStreamsService eventStreamsService;

    @Inject
    private ObjectMapper objectMapper;

    @Outgoing("article-revision-creates")
    public Multi<Record<Long, RevisionCreate>> produceArticleRevisionCreates() {
        return eventStreamsService.streamRevisionCreates()
                .onItem().transform(message -> parse(message, RevisionCreate.class))
                .filter(maybeRevisionCreate -> maybeRevisionCreate.isPresent() && isArticle(maybeRevisionCreate.get()))
                .onItem().transform(maybeRevisionCreate -> {
                    RevisionCreate revisionCreate = maybeRevisionCreate.get();
                    return Record.of(revisionCreate.revisionId, revisionCreate);
                });
    }

    @Outgoing("article-revision-tags-changes")
    public Multi<Record<Long, TagsChange>> produceArticleTagsChanges() {
        return eventStreamsService.streamTagsChanges()
                .onItem().transform(message -> parse(message, TagsChange.class))
                .filter(maybeTagsChange -> {
                    if (maybeTagsChange.isEmpty()) {
                        return false;
                    }
                    TagsChange tagsChange = maybeTagsChange.get();
                    return isArticle(tagsChange) && tagWasAdded(tagsChange, "mw-reverted");
                })
                .onItem().transform(maybeTagsChange -> {
                   TagsChange tagsChange = maybeTagsChange.get();
                   return Record.of(tagsChange.revisionId, tagsChange);
                });
    }

    private <T> Optional<T> parse(String message, Class<T> clazz) {
        try {
            return Optional.of(objectMapper.readValue(message, clazz));
        } catch (JsonProcessingException e) {
            Log.error(e);
            return Optional.empty();
        }
    }

    private static boolean isArticle(MediaWikiEvent event) {
        return event.getNamespaceId() == 0;
    }

    private static boolean tagWasAdded(TagsChange tagsChange, String tag) {
        List<String> oldTags = Arrays.asList(tagsChange.priorState.tags);
        List<String> newTags = Arrays.asList(tagsChange.tags);
        return newTags.contains(tag) && !oldTags.contains(tag);
    }
}
