package ru.job4j.cars.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Model;
import ru.job4j.cars.utils.TransactionalUtil;

import java.util.List;

@Repository
public class ModelRepository extends AbstractCrudRepository<Model, Integer> {

    public ModelRepository(TransactionalUtil tx) {
        super(tx, Model.class, "id");
    }

    public List<Model> findAllOrderByNameAsc() {
        return tx.txResult(session -> session
                .createQuery("from Model order by name asc", Model.class)
                .list());
    }
}
