package org.mdholloway.wikitrends;

import com.heroku.sdk.EnvKeyStore;
import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import io.quarkus.logging.Log;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import java.io.File;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;

import static java.lang.System.getenv;
import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
import static org.apache.kafka.common.config.SslConfigs.*;
import static org.apache.kafka.common.config.SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.*;

@ApplicationScoped
public class StreamAnalyzerApp {

    private KafkaStreams kafkaStreams;

    void onStart(@Observes StartupEvent startupEvent) {
        kafkaStreams = new KafkaStreams(buildTopology(), buildConfig());
        kafkaStreams.start();
    }

    void onStop(@Observes ShutdownEvent shutdownEvent) {
        kafkaStreams.close();
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        ObjectMapperSerde<RevisionCreate> revisionCreateSerde = new ObjectMapperSerde<>(RevisionCreate.class);
        ObjectMapperSerde<TagsChange> tagsChangeSerde = new ObjectMapperSerde<>(TagsChange.class);

        String kafkaPrefix = getenv("KAFKA_PREFIX");

        KStream<Long, RevisionCreate> revisionCreates = builder.stream(
                kafkaPrefix + "article-revision-creates",
                Consumed.with(Serdes.Long(), revisionCreateSerde)
        );
        KStream<Long, TagsChange> tagsChanges = builder.stream(
                kafkaPrefix + "article-revision-tags-changes",
                Consumed.with(Serdes.Long(), tagsChangeSerde)
        );

        KStream<Long, RevisionCreate> revertedRevisions = revisionCreates.join(
                tagsChanges,
                (revisionId, revisionCreate, tagsChange) -> revisionCreate,
                JoinWindows.of(Duration.ofHours(1)),
                StreamJoined.with(Serdes.Long(), revisionCreateSerde, tagsChangeSerde)
        );

        revertedRevisions.to(kafkaPrefix + "reverted-revisions",
                Produced.with(Serdes.Long(), revisionCreateSerde));

        return builder.build();
    }

    private static Properties buildConfig() {
        Properties properties = new Properties();
        String kafkaUrl = getenv("KAFKA_URL");
        String kafkaPrefix = getenv("KAFKA_PREFIX");

        properties.putAll(Map.of(
                BOOTSTRAP_SERVERS_CONFIG, kafkaUrl.isEmpty() ? "localhost:9092" : kafkaUrl,
                APPLICATION_ID_CONFIG, kafkaPrefix + "wikitrends-stream-analysis-app",
                DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName(),
                DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName()
        ));

        try {
            EnvKeyStore envTrustStore = EnvKeyStore.createWithRandomPassword("KAFKA_TRUSTED_CERT");
            EnvKeyStore envKeyStore = EnvKeyStore.createWithRandomPassword("KAFKA_CLIENT_CERT_KEY", "KAFKA_CLIENT_CERT");
            File trustStore = envTrustStore.storeTemp();
            File keyStore = envKeyStore.storeTemp();

            properties.putAll(Map.of(
                    SECURITY_PROTOCOL_CONFIG, "SSL",
                    "ssl.endpoint.identification.algorithm", "",
                    SSL_TRUSTSTORE_TYPE_CONFIG, envTrustStore.type(),
                    SSL_TRUSTSTORE_LOCATION_CONFIG, trustStore.getAbsolutePath(),
                    SSL_TRUSTSTORE_PASSWORD_CONFIG, envTrustStore.password(),
                    SSL_KEYSTORE_TYPE_CONFIG, envKeyStore.type(),
                    SSL_KEYSTORE_LOCATION_CONFIG, keyStore.getAbsolutePath(),
                    SSL_KEYSTORE_PASSWORD_CONFIG, envKeyStore.password()
            ));
        } catch (Exception e) {
            Log.error("Problem creating keystore");
        }

        return properties;
    }
}