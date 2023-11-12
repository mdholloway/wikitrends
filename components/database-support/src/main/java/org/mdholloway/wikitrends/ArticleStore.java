package org.mdholloway.wikitrends;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.value.SetArgs;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ArticleStore {

    @Inject
    private ReactiveRedisDataSource redis;

    public Uni<String> get(String key) {
        return redis.value(String.class).get(key);
    }

    public Uni<Void> set(String key, String value, int ttlSeconds) {
        return redis.value(String.class).set(key, value, new SetArgs().ex(ttlSeconds));
    }

    public Uni<Boolean> exists(String key) {
        return redis.key().exists(key);
    }
}
