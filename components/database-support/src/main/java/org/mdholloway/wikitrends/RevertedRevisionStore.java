package org.mdholloway.wikitrends;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import java.util.List;

@ApplicationScoped
public class RevertedRevisionStore {

    @Inject
    private SessionFactory sessionFactory;

    public Uni<Void> create(RevisionCreate revisionCreate) {
        return sessionFactory.withTransaction(session -> session.persist(RevertedRevision.from(revisionCreate)));
    }

    public Uni<List<RevertedRevision>> getAll() {
        return sessionFactory.withTransaction(session -> {
            CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
            CriteriaQuery<RevertedRevision> query = criteriaBuilder.createQuery(RevertedRevision.class);
            query.from(RevertedRevision.class);
            return session.createQuery(query).getResultList();
        });
    }
}
