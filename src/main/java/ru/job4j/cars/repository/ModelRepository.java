package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Model;

@Repository
public class ModelRepository extends AbstractCrudRepository<Model, Integer> {

    public ModelRepository(SessionFactory sf) {
        super(sf, Model.class, "id");
    }
}
