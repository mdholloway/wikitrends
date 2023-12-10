package org.mdholloway.wikitrends;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class RevisionCreateDeserializer extends ObjectMapperDeserializer<RevisionCreate> {

    public RevisionCreateDeserializer() {
        super(RevisionCreate.class);
    }
}
