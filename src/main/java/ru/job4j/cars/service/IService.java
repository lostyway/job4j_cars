package ru.job4j.cars.service;

import java.io.Serializable;
import java.util.List;

public interface IService<T, ID extends Serializable> {
    T save(T entity);

    boolean updatePostAndCar(T entity, int id);

    void delete(ID id);

    T findById(ID id);

    List<T> findAll();
}
