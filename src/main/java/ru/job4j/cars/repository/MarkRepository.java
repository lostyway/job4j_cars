package ru.job4j.cars.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Mark;
import ru.job4j.cars.utils.TransactionalUtil;

import java.util.List;

@Repository
public class MarkRepository extends AbstractCrudRepository<Mark, Integer> {

    public MarkRepository(TransactionalUtil tx) {
        super(tx, Mark.class, "id");
    }

    public List<Mark> findAllOrderByNameAsc() {
        return tx.txResult(session -> session
                .createQuery("from Mark order by name asc", Mark.class)
                .list());
    }
}
