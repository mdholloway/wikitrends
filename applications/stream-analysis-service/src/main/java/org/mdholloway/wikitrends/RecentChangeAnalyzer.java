package org.mdholloway.wikitrends;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RecentChangeAnalyzer {

    @Inject
    private SentimentChangeAnalyzer sentimentChangeAnalyzer;

    @Startup
    public void start() {
        sentimentChangeAnalyzer.start();
    }
}