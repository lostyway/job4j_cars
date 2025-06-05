package ru.job4j.cars.service;

import ru.job4j.cars.dto.PhotoDto;
import ru.job4j.cars.model.Photo;
import ru.job4j.cars.model.Post;

import java.util.List;
import java.util.Optional;

public interface IPhotoService {
    Photo save(PhotoDto photoDto, Post post);

    Optional<PhotoDto> getPhotoById(int id);

    void deleteById(int id);

    List<PhotoDto> getAll();
}
