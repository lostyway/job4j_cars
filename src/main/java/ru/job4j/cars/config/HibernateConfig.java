package ru.job4j.cars.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {
    final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure()
            .build();

    @Bean(destroyMethod = "close")
    public SessionFactory getSessionFactory() {
        return new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();
    }
}
