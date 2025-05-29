package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@AllArgsConstructor
@Repository
public class UserRepository {
    private final SessionFactory sf;

    /**
     * Сохранить в базе.
     *
     * @param user пользователь.
     * @return пользователь с id.
     */
    public User create(User user) {
        return sessionStartAndEndReturn(session -> {
            session.save(user);
            return user;
        });
    }

    /**
     * Обновить в базе пользователя.
     *
     * @param user пользователь.
     */
    public void update(User user) {
        sessionStartAndEndVoid(session -> session.update(user));
    }

    /**
     * Удалить пользователя по id.
     *
     * @param userId ID
     */
    public void delete(int userId) {
        sessionStartAndEndVoid(session -> {
            User user = session.get(User.class, userId);
            if (user != null) {
                session.delete(user);
            }
        });
    }

    /**
     * Список пользователь отсортированных по id.
     *
     * @return список пользователей.
     */
    public List<User> findAllOrderById() {
        return sessionStartAndEndReturn(session -> session.createQuery("from User order by id", User.class).list());
    }


    /**
     * Найти пользователя по ID
     *
     * @return пользователь.
     */
    public Optional<User> findById(int userId) {
        return sessionStartAndEndReturn(session
                -> session.createQuery("from User where id = :id", User.class)
                .setParameter("id", userId).setMaxResults(1).list().stream().findFirst());
    }

    /**
     * Список пользователей по login LIKE %key%
     *
     * @param key key
     * @return список пользователей.
     */
    public List<User> findByLikeLogin(String key) {
        return sessionStartAndEndReturn(session
                -> session.createQuery("from User where login like :key", User.class)
                .setParameter("key", "%" + key + "%")
                .list());
    }

    /**
     * Найти пользователя по login.
     *
     * @param login login.
     * @return Optional or user.
     */
    public Optional<User> findByLogin(String login) {
        return sessionStartAndEndReturn(session
                -> session.createQuery("from User where login = :login", User.class)
                .setParameter("login", login).list().stream().findFirst());
    }

    private void sessionStartAndEndVoid(Consumer<Session> consumer) {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            consumer.accept(session);
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }
    }

    private <T> T sessionStartAndEndReturn(Function<Session, T> function) {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            T result = function.apply(session);
            session.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }
}