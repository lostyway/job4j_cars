package ru.job4j.cars.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Photo;
import ru.job4j.cars.utils.TransactionalUtil;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PhotoRepository implements IPhotoRepository {
    private final TransactionalUtil tx;


    @Override
    public Photo save(Photo photo) {
        return tx.txResult(session -> {
            session.save(photo);
            return photo;
        });
    }

    @Override
    public Optional<Photo> findById(int id) {
        return Optional.ofNullable(tx.txResult(session -> session.get(Photo.class, id)));
    }

    @Override
    public void deleteById(int id) {
        tx.txVoid(session -> session.delete(session.get(Photo.class, id)));
    }

    @Override
    public List<Photo> findAll() {
        return tx.txResult(session -> session.createQuery("from Photo", Photo.class).list());
    }
}
