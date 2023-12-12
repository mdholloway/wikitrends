package org.mdholloway.wikitrends;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;

import java.util.HashMap;
import java.util.Map;

public class KafkaTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    @Override
    public Map<String, String> start() {
        return new HashMap<>(InMemoryConnector.switchIncomingChannelsToInMemory("reverted-revisions"));
    }

    @Override
    public void stop() {
        InMemoryConnector.clear();
    }
}
