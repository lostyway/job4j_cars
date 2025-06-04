package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Mark;

@Repository
public class MarkRepository extends AbstractCrudRepository<Mark, Integer> {

    public MarkRepository(SessionFactory sf) {
        super(sf, Mark.class, "id");
    }
}
