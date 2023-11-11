package org.mdholloway.wikitrends;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.mdholloway.wikitrends.eventstreams.MessageProcessor;

@QuarkusMain
public class StreamConsumerMain implements QuarkusApplication {

    @Inject
    private MessageProcessor messageProcessor;

    @Override
    public int run(String... args) {
        messageProcessor.start();

        Quarkus.waitForExit();
        return 0;
    }
}
