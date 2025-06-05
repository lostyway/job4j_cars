package ru.job4j.cars.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.User;
import ru.job4j.cars.utils.TransactionalUtil;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends AbstractCrudRepository<User, Integer> {


    public UserRepository(TransactionalUtil tx) {
        super(tx, User.class, "id");
    }

    /**
     * Список пользователей по login LIKE %key%
     *
     * @param key key
     * @return список пользователей.
     */
    public List<User> findByLikeLogin(String key) {
        return tx.txResult(session
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
        return tx.txResult(session
                -> session.createQuery("from User where login = :login", User.class)
                .setParameter("login", login).list().stream().findFirst());
    }

    public Optional<User> findByLoginAndPassword(String login, String password) {
        return tx.txResult(session -> session.createQuery("from User where login = :login and password = :password", User.class)
                .setParameter("login", login)
                .setParameter("password", password)
                .uniqueResultOptional());
    }
}