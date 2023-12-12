package org.mdholloway.wikitrends;

import com.heroku.sdk.EnvKeyStore;
import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Identifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import static org.apache.kafka.clients.CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
import static org.apache.kafka.common.config.SslConfigs.*;

public class SslConfig {

    @Produces
    @ApplicationScoped
    @Identifier("ssl-config")
    public Map<String, Object> sslConfig() {
        try {
            EnvKeyStore envTrustStore = EnvKeyStore.createWithRandomPassword("KAFKA_TRUSTED_CERT");
            EnvKeyStore envKeyStore = EnvKeyStore.createWithRandomPassword("KAFKA_CLIENT_CERT_KEY", "KAFKA_CLIENT_CERT");
            File trustStore = envTrustStore.storeTemp();
            File keyStore = envKeyStore.storeTemp();

            return Map.of(
                    SECURITY_PROTOCOL_CONFIG, "SSL",
                    "ssl.endpoint.identification.algorithm", "",
                    SSL_TRUSTSTORE_TYPE_CONFIG, envTrustStore.type(),
                    SSL_TRUSTSTORE_LOCATION_CONFIG, trustStore.getAbsolutePath(),
                    SSL_TRUSTSTORE_PASSWORD_CONFIG, envTrustStore.password(),
                    SSL_KEYSTORE_TYPE_CONFIG, envKeyStore.type(),
                    SSL_KEYSTORE_LOCATION_CONFIG, keyStore.getAbsolutePath(),
                    SSL_KEYSTORE_PASSWORD_CONFIG, envKeyStore.password()
            );
        } catch (Exception e) {
            Log.error("Problem creating keystore");
            return Collections.emptyMap();
        }
    }
}
