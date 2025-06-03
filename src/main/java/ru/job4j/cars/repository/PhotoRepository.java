package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import ru.job4j.cars.model.Photo;

public class PhotoRepository extends AbstractCrudRepository<Photo, Integer> {

    public PhotoRepository(SessionFactory sf) {
        super(sf, Photo.class, "id");
    }
}
