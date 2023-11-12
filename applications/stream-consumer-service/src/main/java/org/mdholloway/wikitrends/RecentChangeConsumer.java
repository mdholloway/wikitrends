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

class RecentChangeConsumer {

    private static final String ENGLISH_WIKIPEDIA = "enwiki";
    private static final Pattern URI_PATTERN = Pattern.compile("^https://en.wikipedia.org/wiki/(.*)$");

    @RestClient
    private EventStreamsService eventStreamsService;

    @RestClient
    private PageService pageService;

    @Channel("recent-changes-ready-for-analysis")
    private Emitter<StoredRevisions> storedRevisionsEmitter;

    @Inject
    private ArticleStore articleStore;

    @Inject
    private ObjectMapper objectMapper;

    void start() {
        eventStreamsService.streamRecentChanges().subscribe().with(message -> {
            Optional<RecentChange> maybeRecentChange = parse(message);
            if (maybeRecentChange.isEmpty()) {
                return;
            }

            RecentChange recentChange = maybeRecentChange.get();
            if (!isInterestingRevision(recentChange)) {
                return;
            }

            long oldRev = recentChange.revision.oldRev;
            long newRev = recentChange.revision.newRev;
            String uri = recentChange.meta.uri;

            Matcher dbTitleMatcher = URI_PATTERN.matcher(uri);
            if (!dbTitleMatcher.matches()) {
                Log.error("Could not parse dbTitle from article uri: " + uri);
                return;
            }
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
                    .with(result -> storedRevisionsEmitter.send(StoredRevisions.with(
                            key(dbTitle, oldRev),
                            key(dbTitle, newRev)
                    )));
        });
    }

    private Optional<RecentChange> parse(String message) {
        try {
            return Optional.of(objectMapper.readValue(message, RecentChange.class));
        } catch (JsonProcessingException e) {
            Log.error(e);
            return Optional.empty();
        }
    }

    private static boolean isInterestingRevision(RecentChange recentChange) {
        return !recentChange.bot &&
                recentChange.namespace == 0 &&
                recentChange.type.equals("edit") &&
                ENGLISH_WIKIPEDIA.equals(recentChange.wiki);
    }

    private static String key(String dbTitle, long revision) {
        return dbTitle + "/" + revision;
    }
}
