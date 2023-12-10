package org.mdholloway.wikitrends;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class RevertedRevisionStore {

    @Inject
    private EntityManager entityManager;

    @Transactional
    public void create(RevisionCreate revisionCreate) {
        entityManager.persist(RevertedRevision.from(revisionCreate));
    }
}
