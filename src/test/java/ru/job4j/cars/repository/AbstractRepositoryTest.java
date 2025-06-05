package ru.job4j.cars.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import ru.job4j.cars.utils.TransactionalUtil;
import utils.HibernateSessionFactoryUtil;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractRepositoryTest<T> {
    protected SessionFactory sf;
    protected TransactionalUtil tx;
    protected Session session;
    protected Transaction transaction;

    protected abstract String getEntityName();

    @BeforeAll
    void setupFactory() {
        sf = HibernateSessionFactoryUtil.getSessionFactory();
        tx = new TransactionalUtil(sf);
    }

    @BeforeEach
    void openTransactionAndClean() {
        session = sf.openSession();
        transaction = session.beginTransaction();

        session.createQuery("delete from " + getEntityName()).executeUpdate();

        transaction.commit();

        transaction = session.beginTransaction();
    }

    @AfterEach
    void rollbackTransaction() {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
        if (session != null && session.isOpen()) {
            session.close();
        }
    }
}

