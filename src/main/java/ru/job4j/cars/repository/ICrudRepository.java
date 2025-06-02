package ru.job4j.cars.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface ICrudRepository<T, ID extends Serializable> {

    T create(T model);

    void update(T model);

    void delete(ID id);

    List<T> findAllOrderById();

    Optional<T> findById(ID id);
}
