//package ru.job4j.cars.repository;
//
//import org.hibernate.SessionFactory;
//import org.hibernate.boot.MetadataSources;
//import org.hibernate.boot.registry.StandardServiceRegistry;
//import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
//import org.junit.jupiter.api.Test;
//import ru.job4j.cars.model.Car;
//
//import javax.persistence.OptimisticLockException;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//class CarRepositoryTest {
//    @Test
//    void whenAddUser() {
//        try (StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
//             SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
//             CarRepository carRepository = new CarRepository(sf)) {
//
//            Car car = new Car();
//            car.setLogin("test");
//            car.setPassword("passwordTest");
//            Car result = carRepository.create(car);
//            assertThat(result).isEqualTo(car);
//            var optionalFind = carRepository.findById(result.getId());
//            assertThat(optionalFind.isPresent()).isTrue();
//            assertThat(optionalFind.get().getLogin()).isEqualTo(car.getLogin());
//            assertThat(optionalFind.get().getPassword()).isEqualTo(car.getPassword());
//        }
//    }
//
//    @Test
//    void whenAddAndReplaceUser() {
//        try (StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
//             SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
//             CarRepository carRepository = new CarRepository(sf)) {
//
//            Car car = new Car();
//            car.setLogin("test");
//            car.setPassword("passwordTest");
//            Car result = carRepository.create(car);
//            Car userToReplace = new Car();
//            userToReplace.setLogin("replaced");
//            userToReplace.setPassword("replacedPasswordTest");
//            userToReplace.setId(result.getId());
//            carRepository.update(userToReplace);
//            var optionalFind = carRepository.findById(result.getId());
//            assertThat(optionalFind.isPresent()).isTrue();
//            assertThat(optionalFind.get().getLogin()).isEqualTo(userToReplace.getLogin());
//            assertThat(optionalFind.get().getPassword()).isEqualTo(userToReplace.getPassword());
//        }
//    }
//
//    @Test
//    void whenAddAndReplaceWrongUserThenOldUserInDatabaseAndGetException() {
//        try (StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
//             SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
//              CarRepository carRepository = new CarRepository(sf)) {
//
//            Car car = new Car();
//            car.setLogin("test");
//            car.setPassword("passwordTest");
//            Car result = carRepository.create(car);
//            Car userToReplace = new Car();
//            userToReplace.setLogin("replaced");
//            userToReplace.setPassword("replacedPasswordTest");
//            userToReplace.setId(1000);
//            assertThatThrownBy(() -> carRepository.update(userToReplace)).isInstanceOf(OptimisticLockException.class);
//            var optionalFind = carRepository.findById(result.getId());
//            assertThat(optionalFind.isPresent()).isTrue();
//            assertThat(optionalFind.get().getLogin()).isEqualTo(result.getLogin());
//            assertThat(optionalFind.get().getPassword()).isEqualTo(result.getPassword());
//        }
//    }
//
//    @Test
//    void whenDeleteUserIsSuccessfulThenDeleteUser() {
//        try (StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
//             SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
//              CarRepository carRepository = new CarRepository(sf)) {
//
//            Car car = new Car();
//            car.setLogin("test");
//            car.setPassword("passwordTest");
//            Car result = carRepository.create(car);
//            carRepository.delete(result.getId());
//            var optionalFind = carRepository.findById(result.getId());
//            assertThat(optionalFind.isPresent()).isFalse();
//        }
//    }
//
//    @Test
//    void whenFindAllOrderByIdUserIsSuccessful() {
//        try (StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
//             SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
//             CarRepository carRepository = new CarRepository(sf)) {
//
//            Car car = new Car();
//            car.setLogin("test");
//            car.setPassword("passwordTest");
//            Car car2 = new Car();
//            car2.setLogin("test2");
//            car2.setPassword("passwordTest2");
//            Car result2 = carRepository.create(car2);
//            Car result = carRepository.create(car);
//            List<Car> res = carRepository.findAllOrderById();
//            assertThat(res.size()).isEqualTo(2);
//            assertThat(res.get(0)).isEqualTo(result2);
//            assertThat(res.get(1)).isEqualTo(result);
//        }
//    }
//
//}