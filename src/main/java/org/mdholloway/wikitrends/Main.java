package org.mdholloway.wikitrends;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;

@QuarkusMain
public class Main implements QuarkusApplication {

    @Inject
    private MessageProcessor messageProcessor;

    @Override
    public int run(String... args) {
        messageProcessor.processMessages();

        Quarkus.waitForExit();
        return 0;
    }
}
