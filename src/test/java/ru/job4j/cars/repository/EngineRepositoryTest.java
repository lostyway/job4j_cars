package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Engine;

import javax.persistence.OptimisticLockException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EngineRepositoryTest {

    @Test
    void whenAddUser() {
        try(StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            EngineRepository engineRepository = new EngineRepository(sf)) {

            Engine engine = new Engine();
            engine.setName("test");
            Engine result = engineRepository.create(engine);
            assertThat(result).isEqualTo(engine);
            var optionalFind = engineRepository.findById(result.getId());
            assertThat(optionalFind.isPresent()).isTrue();
            assertThat(optionalFind.get().getName()).isEqualTo(engine.getName());
        }
    }

    @Test
    void whenAddAndReplaceUser() {
        try(StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            EngineRepository engineRepository = new EngineRepository(sf)) {

            Engine engine = new Engine();
            engine.setName("test");
            Engine result = engineRepository.create(engine);
            Engine engineToReplace = new Engine();
            engineToReplace.setName("replaced");
            engineToReplace.setId(result.getId());
            engineRepository.update(engineToReplace);
            var optionalFind = engineRepository.findById(result.getId());
            assertThat(optionalFind.isPresent()).isTrue();
            assertThat(optionalFind.get().getName()).isEqualTo(engineToReplace.getName());
        }
    }

    @Test
    void whenAddAndReplaceWrongUserThenOldUserInDatabaseAndGetException() {
        try(StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            EngineRepository engineRepository = new EngineRepository(sf)) {

            Engine engine = new Engine();
            engine.setName("test");
            Engine result = engineRepository.create(engine);
            Engine engineToReplace = new Engine();
            engineToReplace.setName("replaced");
            engineToReplace.setId(1000);
            assertThatThrownBy(() -> engineRepository.update(engineToReplace)).isInstanceOf(OptimisticLockException.class);
            var optionalFind = engineRepository.findById(result.getId());
            assertThat(optionalFind.isPresent()).isTrue();
            assertThat(optionalFind.get().getName()).isEqualTo(result.getName());
        }
    }

    @Test
    void whenDeleteUserIsSuccessfulThenDeleteUser() {
        try(StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            EngineRepository engineRepository = new EngineRepository(sf)) {

            Engine engine = new Engine();
            engine.setName("test");
            Engine result = engineRepository.create(engine);
            engineRepository.delete(result.getId());
            var optionalFind = engineRepository.findById(result.getId());
            assertThat(optionalFind.isPresent()).isFalse();
        }
    }

    @Test
    void whenFindAllOrderByIdUserIsSuccessful() {
        try(StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            EngineRepository engineRepository = new EngineRepository(sf)) {

            Engine engine = new Engine();
            engine.setName("test");
            Engine engine2 = new Engine();
            engine.setName("test2");
            Engine result2 = engineRepository.create(engine2);
            Engine result = engineRepository.create(engine);
            List<Engine> res = engineRepository.findAllOrderById();
            assertThat(res.size()).isEqualTo(2);
            assertThat(res.get(0)).isEqualTo(result2);
            assertThat(res.get(1)).isEqualTo(result);
        }
    }
}