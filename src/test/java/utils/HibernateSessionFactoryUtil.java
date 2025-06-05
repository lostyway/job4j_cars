package utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactoryUtil {
    private static SessionFactory sessionFactory;
    private static final Object LOCK = new Object();
    private static final long LIFETIME_MS = 60_000;
    private static boolean shutdownScheduled = false;

    public static SessionFactory getSessionFactory() {
        synchronized (LOCK) {
            if (sessionFactory == null || sessionFactory.isClosed()) {
                sessionFactory = new Configuration().configure().buildSessionFactory();
                scheduleShutdown();
            }
        }
        return sessionFactory;
    }

    private static void scheduleShutdown() {
        if (shutdownScheduled) {
            return;
        }
        shutdownScheduled = true;
        new Thread(() -> {
           try {
               Thread.sleep(LIFETIME_MS);
               synchronized (LOCK) {
                   if (sessionFactory != null && !sessionFactory.isClosed()) {
                       sessionFactory.close();
                       System.out.println("Сессия закрыта спустя: " + LIFETIME_MS + " секунд");
                   }
               }
           } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
           }
        });
    }
}
