package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Mark;

import java.util.List;

@Repository
public class MarkRepository extends AbstractCrudRepository<Mark, Integer> {

    public MarkRepository(SessionFactory sf) {
        super(sf, Mark.class, "id");
    }

    public List<Mark> findAllOrderByNameAsc() {
        return txReturn(session -> session
                .createQuery("from Mark order by name asc", Mark.class)
                .list());
    }
}
