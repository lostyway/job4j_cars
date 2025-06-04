package ru.job4j.cars.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionalUtil {
    private final SessionFactory sf;

    public void txVoid(Consumer<Session> command) {
        Session session = sf.openSession();
        try (session) {
            session.beginTransaction();
            command.accept(session);
            session.getTransaction().commit();
        } catch (Exception e) {
            rollBack(session);
        }
    }

    public <T> T txResult(Function<Session, T> function) {
        Session session = sf.openSession();
        try (session) {
            session.beginTransaction();
            T result = function.apply(session);
            session.getTransaction().commit();
            return result;
        } catch (Exception e) {
            log.error("Произошла ошибка в транзакции: {}", String.valueOf(e));
            rollBack(session);
            throw e;
        }
    }

    public void rollBack(Session session) {
        if (session != null && session.getTransaction().isActive()) {
            session.getTransaction().rollback();
        }
    }
}
