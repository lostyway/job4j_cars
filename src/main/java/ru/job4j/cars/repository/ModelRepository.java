package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Model;

import java.util.List;

@Repository
public class ModelRepository extends AbstractCrudRepository<Model, Integer> {

    public ModelRepository(SessionFactory sf) {
        super(sf, Model.class, "id");
    }

    public List<Model> findAllOrderByNameAsc() {
        return txReturn(session -> session
                .createQuery("from Model order by name asc", Model.class)
                .list());
    }
}
