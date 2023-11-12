package org.mdholloway.wikitrends;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.mdholloway.wikitrends.eventstreams.EventStreamsService;
import org.mdholloway.wikitrends.eventstreams.model.RecentChangeEvent;

class MessageProcessor {

    private static final String ENGLISH_WIKIPEDIA = "enwiki";
    private static final Pattern URI_PATTERN = Pattern.compile("^https://en.wikipedia.org/wiki/(.*)$");

    @RestClient
    private EventStreamsService eventStreamsService;

    @RestClient
    private PageService pageService;

    @Channel("revision-titles")
    private Emitter<String> revisionTitleEmitter;

    @Inject
    private ArticleStore articleStore;

    @Inject
    private ObjectMapper objectMapper;

    void start() {
        eventStreamsService.streamRecentChangeEvents().subscribe().with(message -> {
            Optional<RecentChangeEvent> maybeRecentChange = parse(message);
            if (maybeRecentChange.isEmpty()) {
                return;
            }

            RecentChangeEvent recentChange = maybeRecentChange.get();
            if (!isInterestingRevision(recentChange)) {
                return;
            }

            int oldRev = recentChange.revision.oldRev;
            int newRev = recentChange.revision.newRev;

            Matcher dbTitleMatcher = URI_PATTERN.matcher(recentChange.meta.uri);
            dbTitleMatcher.matches();
            String dbTitle = dbTitleMatcher.group(1);

            // TODO: Check for already-fetched previous page HTML in storage
            Set<Uni<Void>> fetchAndStoreRequests = Stream.of(oldRev, newRev)
                    .map(revision -> pageService.getPageHtml(dbTitle, revision)
                            .chain(result -> articleStore.set(dbTitle + "/" + revision, result, 86400)))
                    .collect(Collectors.toUnmodifiableSet());

            Uni.combine().all().unis(fetchAndStoreRequests)
                    .discardItems()
                    .log()
                    .subscribe()
                    // TODO: Forward the RevisionCreateEvent to the revision analyzer
                    .with(result -> Log.debug("Fetched and stored " + dbTitle + " revisions " + oldRev + " (prev) and " + newRev + " (cur)"));
        });
    }

    private Optional<RecentChangeEvent> parse(String message) {
        try {
            return Optional.of(objectMapper.readValue(message, RecentChangeEvent.class));
        } catch (JsonProcessingException e) {
            Log.error(e);
            return Optional.empty();
        }
    }

    private static boolean isInterestingRevision(RecentChangeEvent recentChange) {
        return !recentChange.bot &&
                recentChange.namespace == 0 &&
                recentChange.type.equals("edit") &&
                ENGLISH_WIKIPEDIA.equals(recentChange.wiki);
    }
}
