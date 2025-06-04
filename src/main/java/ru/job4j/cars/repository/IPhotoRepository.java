package ru.job4j.cars.repository;

import ru.job4j.cars.model.Photo;

import java.util.List;
import java.util.Optional;

public interface IPhotoRepository {
    Photo save(Photo photo);

    Optional<Photo> findById(int id);

    void deleteById(int id);

    List<Photo> findAll();
}
