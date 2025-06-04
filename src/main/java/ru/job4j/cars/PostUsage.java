package ru.job4j.cars;

import org.hibernate.SessionFactory;
import ru.job4j.cars.config.HibernateConfig;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PostUsage {
    public static void main(String[] args) {
        SessionFactory sf = new HibernateConfig().getSessionFactory();

        var markRepo = new MarkRepository(sf);
        var modelRepo = new ModelRepository(sf);
        var engineRepo = new EngineRepository(sf);
        var carRepo = new CarRepository(sf);
        var userRepo = new UserRepository(sf);
        ICrudRepository<Post, Integer> postRepo = new PostRepository(sf);

        Mark mark = new Mark();
        mark.setName("test");
        markRepo.create(mark);

        Model model = new Model();
        model.setName("testmodel");
        modelRepo.create(model);

        Engine engine = new Engine();
        engine.setName("V6");
        engineRepo.create(engine);

        Car car = Car.builder()
                .name("SuperCar")
                .year(LocalDate.of(2020, 5, 10))
                .mark(mark)
                .model(model)
                .engine(engine)
                .build();
        carRepo.create(car);

        User user = User.builder()
                .login("test")
                .password("123")
                .build();
        userRepo.create(user);

        Post post = Post.builder()
                .description("Selling car")
                .created(LocalDateTime.now())
                .isSold(false)
                .price(350000L)
                .car(car)
                .author(user)
                .build();

        postRepo.create(post);

    }
}
