package org.mdholloway.wikitrends;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import org.apache.kafka.common.serialization.Serializer;

public class RevisionCreateSerializer implements Serializer<RevisionCreate> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, RevisionCreate revisionCreate) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(revisionCreate);
        } catch (JsonProcessingException e) {
            Log.error(e);
            return null;
        }
    }
}
