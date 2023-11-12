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

class RecentChangeConsumer {

    private static final String ENGLISH_WIKIPEDIA = "enwiki";
    private static final Pattern URI_PATTERN = Pattern.compile("^https://en.wikipedia.org/wiki/(.*)$");

    @RestClient
    private EventStreamsService eventStreamsService;

    @RestClient
    private PageService pageService;

    @Channel("recent-changes-ready-for-analysis")
    private Emitter<RecentChangeEvent> readyRecentChangeEmitter;

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

            long oldRev = recentChange.revision.oldRev;
            long newRev = recentChange.revision.newRev;

            Matcher dbTitleMatcher = URI_PATTERN.matcher(recentChange.meta.uri);
            dbTitleMatcher.matches();
            String dbTitle = dbTitleMatcher.group(1);

            Set<Uni<Void>> fetchAndStoreRequests = Stream.of(oldRev, newRev).map(revision ->
                    articleStore.exists(key(dbTitle, revision)).flatMap(revisionExists -> {
                        if (revisionExists) {
                            return Uni.createFrom().voidItem();
                        }
                        return pageService
                                .getPageHtml(dbTitle, revision)
                                .chain(result -> articleStore.set(dbTitle + "/" + revision, result, 86400));
                    })
            ).collect(Collectors.toUnmodifiableSet());

            Uni.combine().all().unis(fetchAndStoreRequests)
                    .discardItems()
                    .log()
                    .subscribe()
                    .with(result -> readyRecentChangeEmitter.send(recentChange));
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

    private static String key(String dbTitle, long revision) {
        return dbTitle + "/" + revision;
    }
}
