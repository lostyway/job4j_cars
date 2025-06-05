package ru.job4j.cars.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor
public abstract class AbstractCrudRepository<T, ID extends Serializable> implements ICrudRepository<T, ID>, AutoCloseable {

    private final SessionFactory sf;
    private final Class<T> modelClass;
    private final String idFieldName;

    @Override
    public T create(T model) {
        return txReturn(session -> {
            session.save(model);
            return model;
        });
    }

    @Override
    public void update(T model) {
        txVoid(session -> session.update(model));
    }

    @Override
    public void delete(ID id) {
        txVoid(session -> {
            T entity = session.get(modelClass, id);
            if (entity != null) {
                session.delete(entity);
            }
        });
    }

    @Override
    public List<T> findAllOrderById() {
        return txReturn(session -> session
                .createQuery("from " + modelClass.getSimpleName() + " order by " + idFieldName, modelClass)
                .list());
    }

    @Override
    public Optional<T> findById(ID id) {
        return txReturn(session -> Optional.ofNullable(session.get(modelClass, id)));
    }

    protected void txVoid(Consumer<Session> command) {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            command.accept(session);
            session.getTransaction().commit();
        } catch (Exception e) {
            rollback(session);
            throw e;
        } finally {
            session.close();
        }
    }

    protected <R> R txReturn(Function<Session, R> command) {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            R result = command.apply(session);
            session.getTransaction().commit();
            return result;
        } catch (Exception e) {
            rollback(session);
            throw e;
        } finally {
            session.close();
        }
    }

    private void rollback(Session session) {
        if (session.getTransaction().isActive()) {
            session.getTransaction().rollback();
        }
    }

    public void close() {
        sf.close();
    }
}
