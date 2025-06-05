package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.utils.TransactionalUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Repository
public abstract class AbstractCrudRepository<T, ID extends Serializable> implements ICrudRepository<T, ID> {

    protected final TransactionalUtil tx;
    private final Class<T> modelClass;
    private final String idFieldName;

    @Override
    public T create(T model) {
        return tx.txResult(session -> {
            session.save(model);
            return model;
        });
    }

    @Override
    public void update(T model) {
        tx.txVoid(session -> session.update(model));
    }

    @Override
    public void delete(ID id) {
        tx.txVoid(session -> {
            T entity = session.get(modelClass, id);
            if (entity != null) {
                session.delete(entity);
            }
        });
    }

    @Override
    public List<T> findAllOrderById() {
        return tx.txResult(session -> session
                .createQuery("from " + modelClass.getSimpleName() + " order by " + idFieldName, modelClass)
                .list());
    }

    @Override
    public Optional<T> findById(ID id) {
        return tx.txResult(session -> Optional.ofNullable(session.get(modelClass, id)));
    }
}
