package utils;

import ru.job4j.cars.model.*;

import java.time.LocalDateTime;
import java.util.Set;

public class TestEntityFactory {

    public static User newUser(String login, String password) {
        return User.builder()
                .login(login)
                .password(password)
                .build();
    }

    public static Model newModel(String name) {
        return Model.builder()
                .name(name)
                .build();
    }

    public static Mark newMark(String name) {
        return Mark.builder()
                .name(name)
                .build();
    }

    public static Engine newEngine(String name) {
        return Engine.builder()
                .name(name)
                .build();
    }

    public static Car newCar(String year, Mark mark, Model model, Engine engine) {
        return Car.builder()
                .year(year)
                .mark(mark)
                .model(model)
                .engine(engine)
                .build();
    }

    public static Set<Photo> newPhotos() {
        return Set.of();
    }

    public static Set<PriceHistory> newPriceHistory() {
        return Set.of();
    }

    public static Post newPost(String description, LocalDateTime created, long price, boolean sold,
                               User author, Set<Photo> photoSet, Set<PriceHistory> priceHistories, Car car) {
        return Post.builder()
                .description(description)
                .created(created)
                .price(price)
                .sold(sold)
                .author(author)
                .photos(photoSet)
                .priceHistory(priceHistories)
                .car(car)
                .build();
    }
}
