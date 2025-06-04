package ru.job4j.cars.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Photo;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Repository
@RequiredArgsConstructor
public class PhotoRepository {
    private final SessionFactory sf;

    public Photo save(Photo photo) {
        txVoid(session -> session.save(photo));
        return photo;
    }

    public Optional<Photo> findById(int id) {
        return Optional.ofNullable(txReturn(session -> session.get(Photo.class, id)));
    }

    public void deleteById(int id) {
        txVoid(session -> {
            Photo photoToDelete = session.get(Photo.class, id);
            if (photoToDelete != null) {
                session.delete(photoToDelete);
            }
        });
    }

    public void txVoid(Consumer<Session> command) {
        Session session = sf.openSession();
        try (session) {
            session.beginTransaction();
            command.accept(session);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    public Photo txReturn(Function<Session, Photo> function) {
        Session session = sf.openSession();
        Photo result;
        try (session) {
            session.beginTransaction();
            result = function.apply(session);
            session.getTransaction().commit();
            return result;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
    }
}
