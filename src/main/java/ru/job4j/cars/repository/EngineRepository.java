package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Engine;

@Repository
public class EngineRepository extends AbstractCrudRepository<Engine, Integer> {

    public EngineRepository(SessionFactory sf) {
        super(sf, Engine.class, "id");
    }
}
