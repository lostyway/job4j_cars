package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Owner;

@Repository
public class OwnerRepository extends AbstractCrudRepository<Owner, Integer> {

    public OwnerRepository(SessionFactory sf) {
        super(sf, Owner.class, "id");
    }
}
