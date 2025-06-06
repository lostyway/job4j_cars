package ru.job4j.cars.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.*;
import utils.TestEntityFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PostRepositoryTest extends AbstractRepositoryTest<Post> {
    private PostRepository postRepository;
    private UserRepository userRepository;
    private CarRepository carRepository;
    private ModelRepository modelRepository;
    private MarkRepository markRepository;
    private EngineRepository engineRepository;

    @BeforeAll
    void setUp() {
        postRepository = new PostRepository(tx);
        userRepository = new UserRepository(tx);
        carRepository = new CarRepository(tx);
        modelRepository = new ModelRepository(tx);
        markRepository = new MarkRepository(tx);
        engineRepository = new EngineRepository(tx);
    }

    @Test
    void whenFindPostById() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        User user = TestEntityFactory.newUser("login", "pass");
        userRepository.create(user);
        Mark mark = TestEntityFactory.newMark("testMark");
        markRepository.create(mark);
        Model model = TestEntityFactory.newModel("testModel");
        modelRepository.create(model);
        Engine engine = TestEntityFactory.newEngine("testEngine");
        engineRepository.create(engine);
        Car car = TestEntityFactory.newCar("2002", mark, model, engine);
        carRepository.create(car);
        Post post = TestEntityFactory.newPost("desc", now, 2000L, false, user, Set.of(), Set.of(), car);
        Post savedPost = postRepository.create(post);
        int id = savedPost.getId();
        Optional<Post> result = postRepository.findPostById(id);
        assertThat(result).isPresent();
        Post postSaved = result.get();
        assertThat(postSaved.getId()).isEqualTo(id);
        assertThat(postSaved.getAuthor()).isEqualTo(user);
        assertThat(postSaved.getDescription()).isEqualTo(post.getDescription());
        assertThat(postSaved.getCreated().withSecond(0)).isEqualTo(post.getCreated().withSecond(0));
        assertThat(postSaved.getCar()).isEqualTo(post.getCar());
    }

    @Override
    protected String getEntityName() {
        return "Post";
    }
}