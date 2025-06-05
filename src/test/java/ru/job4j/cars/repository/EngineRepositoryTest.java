package ru.job4j.cars.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Engine;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EngineRepositoryTest extends AbstractRepositoryTest<Engine> {
    private EngineRepository engineRepository;

    @Override
    protected String getEntityName() {
        return "Engine";
    }

    @BeforeAll
    void init() {
        engineRepository = new EngineRepository(tx);
    }

    @Test
    void whenAddUser() {
        Engine engine = new Engine();
        engine.setName("test");
        Engine result = engineRepository.create(engine);
        assertThat(result).isEqualTo(engine);
        var optionalFind = engineRepository.findById(result.getId());
        assertThat(optionalFind.isPresent()).isTrue();
        assertThat(optionalFind.get().getName()).isEqualTo(engine.getName());
    }

    @Test
    void whenAddAndReplaceUser() {
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

    @Test
    void whenAddAndReplaceWrongUserThenOldUserInDatabaseAndGetException() {
        Engine engine = new Engine();
        engine.setName("test");
        Engine result = engineRepository.create(engine);
        Engine engineToReplace = new Engine();
        engineToReplace.setName("replaced");
        engineToReplace.setId(1000);
        assertThatThrownBy(() -> engineRepository.update(engineToReplace)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Rollback");
        var optionalFind = engineRepository.findById(result.getId());
        assertThat(optionalFind.isPresent()).isTrue();
        assertThat(optionalFind.get().getName()).isEqualTo(result.getName());
    }

    @Test
    void whenDeleteUserIsSuccessfulThenDeleteUser() {
        Engine engine = new Engine();
        engine.setName("test");
        Engine result = engineRepository.create(engine);
        engineRepository.delete(result.getId());
        var optionalFind = engineRepository.findById(result.getId());
        assertThat(optionalFind.isPresent()).isFalse();
    }

    @Test
    void whenFindAllOrderByIdUserIsSuccessful() {
        Engine engine = new Engine();
        engine.setName("test");
        Engine engine2 = new Engine();
        engine2.setName("test2");
        Engine result2 = engineRepository.create(engine2);
        Engine result = engineRepository.create(engine);
        List<Engine> res = engineRepository.findAllOrderById();
        assertThat(res.size()).isEqualTo(2);
        assertThat(res.get(0)).isEqualTo(result2);
        assertThat(res.get(1)).isEqualTo(result);
    }
}