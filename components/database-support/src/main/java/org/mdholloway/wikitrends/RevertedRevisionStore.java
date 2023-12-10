package org.mdholloway.wikitrends;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

@ApplicationScoped
public class RevertedRevisionStore {

    @Inject
    private SessionFactory sessionFactory;

    public Uni<Void> create(RevisionCreate revisionCreate) {
        return sessionFactory.withTransaction(session -> session.persist(RevertedRevision.from(revisionCreate)));
    }
}
