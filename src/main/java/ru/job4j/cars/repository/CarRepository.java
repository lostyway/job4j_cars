package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Car;

@Repository
public class CarRepository extends AbstractCrudRepository<Car, Integer> {

    public CarRepository(SessionFactory sf) {
        super(sf, Car.class, "id");
    }
}
