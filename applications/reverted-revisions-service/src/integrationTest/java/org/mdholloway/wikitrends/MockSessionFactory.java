package org.mdholloway.wikitrends;

import io.quarkus.test.Mock;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.metamodel.Metamodel;
import org.hibernate.Cache;
import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.hibernate.stat.Statistics;

import java.util.function.BiFunction;
import java.util.function.Function;

@Mock
@ApplicationScoped
public class MockSessionFactory implements SessionFactory {
    @Override
    public Uni<Mutiny.Session> openSession() {
        return null;
    }

    @Override
    public Uni<Mutiny.Session> openSession(String s) {
        return null;
    }

    @Override
    public Uni<Mutiny.StatelessSession> openStatelessSession() {
        return null;
    }

    @Override
    public Uni<Mutiny.StatelessSession> openStatelessSession(String s) {
        return null;
    }

    @Override
    public <T> Uni<T> withSession(Function<Mutiny.Session, Uni<T>> function) {
        return null;
    }

    @Override
    public <T> Uni<T> withSession(String s, Function<Mutiny.Session, Uni<T>> function) {
        return null;
    }

    @Override
    public <T> Uni<T> withTransaction(BiFunction<Mutiny.Session, Mutiny.Transaction, Uni<T>> biFunction) {
        return null;
    }

    @Override
    public <T> Uni<T> withStatelessTransaction(BiFunction<Mutiny.StatelessSession, Mutiny.Transaction, Uni<T>> biFunction) {
        return null;
    }

    @Override
    public <T> Uni<T> withStatelessSession(Function<Mutiny.StatelessSession, Uni<T>> function) {
        return null;
    }

    @Override
    public <T> Uni<T> withStatelessSession(String s, Function<Mutiny.StatelessSession, Uni<T>> function) {
        return null;
    }

    @Override
    public <T> Uni<T> withTransaction(String s, BiFunction<Mutiny.Session, Mutiny.Transaction, Uni<T>> biFunction) {
        return null;
    }

    @Override
    public <T> Uni<T> withStatelessTransaction(String s, BiFunction<Mutiny.StatelessSession, Mutiny.Transaction, Uni<T>> biFunction) {
        return null;
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return null;
    }

    @Override
    public Metamodel getMetamodel() {
        return null;
    }

    @Override
    public Cache getCache() {
        return null;
    }

    @Override
    public Statistics getStatistics() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isOpen() {
        return false;
    }
}
