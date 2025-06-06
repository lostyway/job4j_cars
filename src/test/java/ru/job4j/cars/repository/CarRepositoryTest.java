package ru.job4j.cars.repository;

import ru.job4j.cars.model.Car;

class CarRepositoryTest extends AbstractRepositoryTest<Car> {

    @Override
    protected String getEntityName() {
        return "Car";
    }
}