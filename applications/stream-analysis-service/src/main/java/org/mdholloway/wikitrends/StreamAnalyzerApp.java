package org.mdholloway.wikitrends;

import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;

@ApplicationScoped
public class StreamAnalyzerApp {

    @Inject
    RevertedRevisionStore revertedRevisionStore;

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

        KStream<Long, RevertedRevision> revertedRevisions = revisionCreates.join(
                tagsChanges,
                (revisionId, revisionCreate, tagsChange) -> RevertedRevision.from(revisionCreate, tagsChange),
                JoinWindows.of(Duration.ofHours(1)),
                StreamJoined.with(Serdes.Long(), revisionCreateSerde, tagsChangeSerde)
        ); //.to("reverted-article-revisions");

        revertedRevisions.print(Printed.<Long, RevertedRevision>toSysOut().withLabel("reverted-revisions"));

        revertedRevisions.foreach((revisionId, revertedRevision) -> revertedRevisionStore.create(revertedRevision));

        return builder.build();
    }
}