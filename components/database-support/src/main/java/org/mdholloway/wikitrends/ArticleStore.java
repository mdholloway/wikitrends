package org.mdholloway.wikitrends;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.value.ReactiveValueCommands;
import io.quarkus.redis.datasource.value.SetArgs;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ArticleStore {

    private final ReactiveValueCommands<String, String> commands;

    @Inject
    public ArticleStore(ReactiveRedisDataSource redis) {
        this.commands = redis.value(String.class);
    }

    public Uni<String> get(String key) {
        return commands.get(key);
    }

    public Uni<Void> set(String key, String value, int ttlSeconds) {
        return commands.set(key, value, new SetArgs().ex(ttlSeconds));
    }
}
