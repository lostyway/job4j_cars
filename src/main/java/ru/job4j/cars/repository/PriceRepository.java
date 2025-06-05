package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.PriceHistory;

@Repository
public class PriceRepository extends AbstractCrudRepository<PriceHistory, Integer> {

    public PriceRepository(SessionFactory sf) {
        super(sf, PriceHistory.class, "id");
    }
}
