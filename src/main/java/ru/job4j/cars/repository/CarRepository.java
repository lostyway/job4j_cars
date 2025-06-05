package ru.job4j.cars.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.utils.TransactionalUtil;

@Repository
public class CarRepository extends AbstractCrudRepository<Car, Integer> {

    public CarRepository(TransactionalUtil tx) {
        super(tx, Car.class, "id");
    }
}
