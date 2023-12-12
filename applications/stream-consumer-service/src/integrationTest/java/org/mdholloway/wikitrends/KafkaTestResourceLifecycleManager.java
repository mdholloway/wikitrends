package org.mdholloway.wikitrends;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;

import java.util.HashMap;
import java.util.Map;

public class KafkaTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    @Override
    public Map<String, String> start() {
        Map<String, String> env = new HashMap<>();
        env.putAll(InMemoryConnector.switchOutgoingChannelsToInMemory("article-revision-creates"));
        env.putAll(InMemoryConnector.switchOutgoingChannelsToInMemory("article-revision-tags-changes"));
        return env;
    }

    @Override
    public void stop() {
        InMemoryConnector.clear();
    }
}
