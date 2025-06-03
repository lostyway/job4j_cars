package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.User;

import javax.persistence.OptimisticLockException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserRepositoryTest {

    @Test
    void whenAddUser() {
        try(StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            UserRepository userRepository = new UserRepository(sf)) {

            User user = new User();
            user.setLogin("test");
            user.setPassword("passwordTest");
            User result = userRepository.create(user);
            assertThat(result).isEqualTo(user);
            var optionalFind = userRepository.findById(result.getId());
            assertThat(optionalFind.isPresent()).isTrue();
            assertThat(optionalFind.get().getLogin()).isEqualTo(user.getLogin());
            assertThat(optionalFind.get().getPassword()).isEqualTo(user.getPassword());
        }
    }

    @Test
    void whenAddAndReplaceUser() {
        try(StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            UserRepository userRepository = new UserRepository(sf)) {

            User user = new User();
            user.setLogin("test");
            user.setPassword("passwordTest");
            User result = userRepository.create(user);
            User userToReplace = new User();
            userToReplace.setLogin("replaced");
            userToReplace.setPassword("replacedPasswordTest");
            userToReplace.setId(result.getId());
            userRepository.update(userToReplace);
            var optionalFind = userRepository.findById(result.getId());
            assertThat(optionalFind.isPresent()).isTrue();
            assertThat(optionalFind.get().getLogin()).isEqualTo(userToReplace.getLogin());
            assertThat(optionalFind.get().getPassword()).isEqualTo(userToReplace.getPassword());
        }
    }

    @Test
    void whenAddAndReplaceWrongUserThenOldUserInDatabaseAndGetException() {
        try(StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            UserRepository userRepository = new UserRepository(sf)) {

            User user = new User();
            user.setLogin("test");
            user.setPassword("passwordTest");
            User result = userRepository.create(user);
            User userToReplace = new User();
            userToReplace.setLogin("replaced");
            userToReplace.setPassword("replacedPasswordTest");
            userToReplace.setId(1000);
            assertThatThrownBy(() -> userRepository.update(userToReplace)).isInstanceOf(OptimisticLockException.class);
            var optionalFind = userRepository.findById(result.getId());
            assertThat(optionalFind.isPresent()).isTrue();
            assertThat(optionalFind.get().getLogin()).isEqualTo(result.getLogin());
            assertThat(optionalFind.get().getPassword()).isEqualTo(result.getPassword());
        }
    }

    @Test
    void whenDeleteUserIsSuccessfulThenDeleteUser() {
        try(StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            UserRepository userRepository = new UserRepository(sf)) {

            User user = new User();
            user.setLogin("test");
            user.setPassword("passwordTest");
            User result = userRepository.create(user);
            userRepository.delete(result.getId());
            var optionalFind = userRepository.findById(result.getId());
            assertThat(optionalFind.isPresent()).isFalse();
        }
    }

    @Test
    void whenFindAllOrderByIdUserIsSuccessful() {
        try(StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            UserRepository userRepository = new UserRepository(sf)) {

            User user = new User();
            user.setLogin("test");
            user.setPassword("passwordTest");
            User user2 = new User();
            user2.setLogin("test2");
            user2.setPassword("passwordTest2");
            User result2 = userRepository.create(user2);
            User result = userRepository.create(user);
            List<User> res = userRepository.findAllOrderById();
            assertThat(res.size()).isEqualTo(2);
            assertThat(res.get(0)).isEqualTo(result2);
            assertThat(res.get(1)).isEqualTo(result);
        }
    }

    @Test
    void whenFindByLikeLoginUserIsSuccessful() {
        try(StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            UserRepository userRepository = new UserRepository(sf)) {

            User user = new User();
            user.setLogin("1test3");
            user.setPassword("passwordTest");
            User user2 = new User();
            user2.setLogin("2test");
            user2.setPassword("passwordTest2");
            User user3 = new User();
            user3.setLogin("tet");
            user3.setPassword("passwordTest2");
            User result = userRepository.create(user);
            User result2 = userRepository.create(user2);
            userRepository.create(user3);
            List<User> res = userRepository.findByLikeLogin("test");
            assertThat(res.size()).isEqualTo(2);
            assertThat(res.get(0)).isEqualTo(result);
            assertThat(res.get(1)).isEqualTo(result2);
        }
    }

    @Test
    void whenFindByLoginUserIsSuccessful() {
        try(StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            UserRepository userRepository = new UserRepository(sf)) {

            User user = new User();
            user.setLogin("1test3");
            user.setPassword("passwordTest");
            User user2 = new User();
            user2.setLogin("2test");
            user2.setPassword("passwordTest2");
            User user3 = new User();
            user3.setLogin("tet");
            user3.setPassword("passwordTest2");
            User result = userRepository.create(user);
            User result2 = userRepository.create(user2);
            userRepository.create(user3);
            List<User> res = userRepository.findByLikeLogin("1test3");
            assertThat(res.size()).isEqualTo(1);
            assertThat(res.get(0)).isEqualTo(result);
        }
    }
}