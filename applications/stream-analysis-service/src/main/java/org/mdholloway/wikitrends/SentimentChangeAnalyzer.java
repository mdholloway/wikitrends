package org.mdholloway.wikitrends;

import static io.smallrye.mutiny.infrastructure.Infrastructure.getDefaultWorkerPool;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SentimentChangeAnalyzer {

    private static final ScheduledExecutorService defaultWorkerPool = getDefaultWorkerPool();

    @Channel("recent-changes-ready-for-analysis")
    private Multi<StoredRevisions> storedRevisionsForAnalysis;

    @Inject
    private ArticleStore articleStore;

    void start() {
        storedRevisionsForAnalysis.subscribe().with(storedRevisions -> {
            String oldKey = storedRevisions.oldKey;
            String newKey = storedRevisions.newKey;

            List<Uni<String>> retrieveAndAnalyzeRequests = Stream.of(oldKey, newKey).map(key -> articleStore.get(key)
                            .onItem().transform(Jsoup::parse).runSubscriptionOn(defaultWorkerPool)
                            // TODO: Trim off page cruft and/or select for interesting bits (paragraph text)
                            .onItem().transform(Element::text).runSubscriptionOn(defaultWorkerPool)
            ).collect(Collectors.toUnmodifiableList());

            Uni.combine().all().unis(retrieveAndAnalyzeRequests.get(0), retrieveAndAnalyzeRequests.get(1))
                    .asTuple()
                    .subscribe()
                    .with(oldAndNewText -> {
                        Log.debug(oldKey + " (OLD): " + oldAndNewText.getItem1());
                        Log.debug(newKey + " (NEW): " + oldAndNewText.getItem2());
                    });
        });
    }
}
