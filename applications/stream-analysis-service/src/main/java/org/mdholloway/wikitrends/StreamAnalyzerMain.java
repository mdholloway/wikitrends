package org.mdholloway.wikitrends;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.apache.flink.ml.clustering.agglomerativeclustering.AgglomerativeClustering;
import org.apache.flink.ml.clustering.agglomerativeclustering.AgglomerativeClusteringParams;
import org.apache.flink.ml.common.distance.EuclideanDistanceMeasure;
import org.apache.flink.ml.linalg.DenseVector;
import org.apache.flink.ml.linalg.Vectors;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;

@QuarkusMain
public class StreamAnalyzerMain implements QuarkusApplication {

    private final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    private final StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

    @Override
    public int run(String... args) {
        /* KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("http://localhost:9092")
                .setTopics("revision-titles")
                .setGroupId("revision-titles-consumer")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStream<String> revisionTitlesStream =
                env.fromSource(source, WatermarkStrategy.noWatermarks(), "Revision Titles");
        Table revisionTitles = tEnv.fromDataStream(revisionTitlesStream).as("titles");

        AgglomerativeClustering agglomerativeClustering =
                new AgglomerativeClustering()
                        .setLinkage(AgglomerativeClusteringParams.LINKAGE_WARD)
                        .setDistanceMeasure(EuclideanDistanceMeasure.NAME)
                        .setPredictionCol("prediction");

        Table[] outputs = agglomerativeClustering.transform(revisionTitles); */

        // Generates input data.
        DataStream<DenseVector> inputStream =
                env.fromElements(
                        Vectors.dense(1, 1),
                        Vectors.dense(1, 4),
                        Vectors.dense(1, 0),
                        Vectors.dense(4, 1.5),
                        Vectors.dense(4, 4),
                        Vectors.dense(4, 0));
        Table inputTable = tEnv.fromDataStream(inputStream).as("features");

        // Creates an AgglomerativeClustering object and initializes its parameters.
        AgglomerativeClustering agglomerativeClustering =
                new AgglomerativeClustering()
                        .setLinkage(AgglomerativeClusteringParams.LINKAGE_WARD)
                        .setDistanceMeasure(EuclideanDistanceMeasure.NAME)
                        .setPredictionCol("prediction");

        // Uses the AgglomerativeClustering object for clustering.
        Table[] outputs = agglomerativeClustering.transform(inputTable);

        // Extracts and displays the results.
        for (CloseableIterator<Row> it = outputs[0].execute().collect(); it.hasNext(); ) {
            Row row = it.next();
            DenseVector features =
                    (DenseVector) row.getField(agglomerativeClustering.getFeaturesCol());
            int clusterId = (Integer) row.getField(agglomerativeClustering.getPredictionCol());
            System.out.printf("Features: %s \tCluster ID: %s\n", features, clusterId);
        }

        Quarkus.waitForExit();
        return 0;
    }
}