package org.mdholloway.wikitrends;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import java.util.List;

@ApplicationScoped
public class RevertedRevisionStore {

    @Inject
    private SessionFactory sessionFactory;

    public Uni<Void> create(RevisionCreate revisionCreate) {
        return sessionFactory.withTransaction(session -> session.persist(RevertedRevision.from(revisionCreate)));
    }

    public Uni<List<RevertedRevision>> getLast(int limit) {
        return sessionFactory.withTransaction(session -> {
            CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
            CriteriaQuery<RevertedRevision> query = criteriaBuilder.createQuery(RevertedRevision.class);
            Root<RevertedRevision> root = query.from(RevertedRevision.class);
            query.orderBy(criteriaBuilder.desc(root.get("id")));
            return session.createQuery(query).setFirstResult(0).setMaxResults(limit).getResultList();
        });
    }
}
