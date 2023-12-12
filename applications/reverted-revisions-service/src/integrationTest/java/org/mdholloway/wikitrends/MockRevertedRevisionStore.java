package org.mdholloway.wikitrends;

import io.quarkus.test.InjectMock;
import io.quarkus.test.Mock;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.ArrayList;
import java.util.List;

@Mock
@ApplicationScoped
public class MockRevertedRevisionStore extends RevertedRevisionStore {

    @Override
    public Uni<Void> create(RevisionCreate revisionCreate) {
        return Uni.createFrom().voidItem();
    }

    @Override
    public Uni<List<RevertedRevision>> getLast(int limit) {
        List<RevertedRevision> result = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            result.add(buildRevertedRevision(i));
        }
        return Uni.createFrom().item(result);
    }

    private static RevertedRevision buildRevertedRevision(int id) {
        return new RevertedRevision.Builder()
                .setRevisionId(id)
                .setWiki("enwiki")
                .setPageId(1)
                .setPageTitle("Test")
                .build();
    }
}
