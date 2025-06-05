package ru.job4j.cars.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.utils.TransactionalUtil;

@Repository
public class PriceRepository extends AbstractCrudRepository<PriceHistory, Integer> {

    public PriceRepository(TransactionalUtil tx) {
        super(tx, PriceHistory.class, "id");
    }
}
