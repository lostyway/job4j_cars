package ru.job4j.cars.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.utils.TransactionalUtil;

import java.util.List;

@Repository
public class EngineRepository extends AbstractCrudRepository<Engine, Integer> {

    public EngineRepository(TransactionalUtil tx) {
        super(tx, Engine.class, "id");
    }

    public List<Engine> findAllOrderByNameAsc() {
        return tx.txResult(session -> session
                .createQuery("from Engine order by name asc", Engine.class)
                .list());
    }
}
