package org.mdholloway.wikitrends;

import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;

@ApplicationScoped
public class StreamAnalyzerApp {

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        ObjectMapperSerde<RevisionCreate> revisionCreateSerde = new ObjectMapperSerde<>(RevisionCreate.class);
        ObjectMapperSerde<TagsChange> tagsChangeSerde = new ObjectMapperSerde<>(TagsChange.class);

        KStream<Long, RevisionCreate> revisionCreates = builder.stream(
                "article-revision-creates",
                Consumed.with(Serdes.Long(), revisionCreateSerde)
        );
        KStream<Long, TagsChange> tagsChanges = builder.stream(
                "article-revision-tags-changes",
                Consumed.with(Serdes.Long(), tagsChangeSerde)
        );

        revisionCreates.join(
                tagsChanges,
                (revisionId, revisionCreate, tagsChange) -> revisionCreate,
                JoinWindows.of(Duration.ofHours(1)),
                StreamJoined.with(Serdes.Long(), revisionCreateSerde, tagsChangeSerde)
        ).print(Printed.<Long, RevisionCreate>toSysOut().withLabel("reverted-article-revisions"));  //.to("reverted-article-revisions");

        return builder.build();
    }
}