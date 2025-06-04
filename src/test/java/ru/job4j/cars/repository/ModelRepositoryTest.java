package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Model;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ModelRepositoryTest {
    private static ModelRepository modelRepository;
    private static StandardServiceRegistry registry;
    private static SessionFactory sf;

    @BeforeEach
    void setUp() {
        registry = new StandardServiceRegistryBuilder().configure().build();
        sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        modelRepository = new ModelRepository(sf);
    }

    @AfterEach
    void tearDown() {
        if (sf != null) {
            sf.close();
        }

        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @Test
    void whenAddUser() {
        Model mark = Model.builder()
                .name("testMark")
                .build();

        Model result = modelRepository.create(mark);
        assertThat(result).isEqualTo(mark);
        var optionalFind = modelRepository.findById(result.getId());

        assertThat(optionalFind.isPresent()).isTrue();
        assertThat(optionalFind.get().getName()).isEqualTo(mark.getName());
    }


    @Test
    void whenFindAllOrderByIdMarkIsSuccessful() {
        Model model = Model.builder()
                .name("testMark")
                .build();

        Model model2 = Model.builder()
                .name("test2")
                .build();

        Model result2 = modelRepository.create(model2);
        Model result = modelRepository.create(model);
        List<Model> res = modelRepository.findAllOrderById();

        assertThat(res.size()).isEqualTo(2);
        assertThat(res.get(0)).isEqualTo(result2);
        assertThat(res.get(1)).isEqualTo(result);
    }

    @Test
    void whenFindAllOrderByNameAscMarkIsSuccessful() {
        Model model = Model.builder()
                .name("AbText")
                .build();

        Model model2 = Model.builder()
                .name("AATest")
                .build();

        Model model3 = Model.builder()
                .name("BTest")
                .build();

        Model model4 = Model.builder()
                .name("Ztest")
                .build();

        modelRepository.create(model);
        modelRepository.create(model2);
        modelRepository.create(model3);
        modelRepository.create(model4);
        List<Model> res = modelRepository.findAllOrderByNameAsc();

        assertThat(res.size()).isEqualTo(4);
        assertThat(res).isEqualTo(List.of(model2, model, model3, model4));
    }
}