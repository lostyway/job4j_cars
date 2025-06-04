package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.Model;

import java.util.List;

@Repository
public class EngineRepository extends AbstractCrudRepository<Engine, Integer> {

    public EngineRepository(SessionFactory sf) {
        super(sf, Engine.class, "id");
    }

    public List<Engine> findAllOrderByNameAsc() {
        return txReturn(session -> session
                .createQuery("from Engine order by name asc", Engine.class)
                .list());
    }
}
