package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Mark;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MarkRepositoryTest {
    private static MarkRepository markRepository;
    private static StandardServiceRegistry registry;
    private static SessionFactory sf;

    @BeforeEach
    void setUp() {
        registry = new StandardServiceRegistryBuilder().configure().build();
        sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        markRepository = new MarkRepository(sf);
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
        Mark mark = Mark.builder()
                .name("testMark")
                .build();

        Mark result = markRepository.create(mark);
        assertThat(result).isEqualTo(mark);
        var optionalFind = markRepository.findById(result.getId());

        assertThat(optionalFind.isPresent()).isTrue();
        assertThat(optionalFind.get().getName()).isEqualTo(mark.getName());
    }


    @Test
    void whenFindAllOrderByIdMarkIsSuccessful() {
        Mark mark = Mark.builder()
                .name("testMark")
                .build();

        Mark mark2 = Mark.builder()
                .name("test2")
                .build();

        Mark result2 = markRepository.create(mark2);
        Mark result = markRepository.create(mark);
        List<Mark> res = markRepository.findAllOrderById();

        assertThat(res.size()).isEqualTo(2);
        assertThat(res.get(0)).isEqualTo(result2);
        assertThat(res.get(1)).isEqualTo(result);
    }

    @Test
    void whenFindAllOrderByNameAscMarkIsSuccessful() {
        Mark mark = Mark.builder()
                .name("AbText")
                .build();

        Mark mark2 = Mark.builder()
                .name("AATest")
                .build();

        Mark mark3 = Mark.builder()
                .name("BTest")
                .build();

        Mark mark4 = Mark.builder()
                .name("Ztest")
                .build();

        markRepository.create(mark);
        markRepository.create(mark2);
        markRepository.create(mark3);
        markRepository.create(mark4);
        List<Mark> res = markRepository.findAllOrderByNameAsc();

        assertThat(res.size()).isEqualTo(4);
        assertThat(res).isEqualTo(List.of(mark2, mark, mark3, mark4));
    }
}
