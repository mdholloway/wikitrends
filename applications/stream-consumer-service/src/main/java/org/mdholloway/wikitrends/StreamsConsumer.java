package org.mdholloway.wikitrends;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class StreamsConsumer {

    @Inject
    private RecentChangeConsumer recentChangeConsumer;

    @Startup
    public void start() {
        recentChangeConsumer.start();
    }
}
