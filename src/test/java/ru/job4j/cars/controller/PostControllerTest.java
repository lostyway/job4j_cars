package ru.job4j.cars.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cars.GlobalExceptionMessage;
import ru.job4j.cars.exception.NotFoundException;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.Mark;
import ru.job4j.cars.model.Model;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.EngineRepository;
import ru.job4j.cars.repository.MarkRepository;
import ru.job4j.cars.repository.ModelRepository;
import ru.job4j.cars.service.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostControllerTest {
    private PostController mainController;
    private CarService carService;
    private MarkRepository markRepository;
    private ModelRepository modelRepository;
    private UserService userService;
    private PostService postService;
    private EngineRepository engineRepository;
    private PhotoService photoService;
    private PriceService priceService;
    private final String EXCEPTION_TEXT = GlobalExceptionMessage.GLOBAL_EXCEPTION_MESSAGE.getMessage();

    @BeforeEach
    void setUp() {
        carService = mock(CarService.class);
        markRepository = mock(MarkRepository.class);
        modelRepository = mock(ModelRepository.class);
        userService = mock(UserService.class);
        postService = mock(PostService.class);
        engineRepository = mock(EngineRepository.class);
        photoService = mock(PhotoService.class);
        priceService = mock(PriceService.class);
        mainController = new PostController(carService, markRepository, modelRepository,
                userService, postService, engineRepository, photoService, priceService);
    }

    @Test
    void whenGetIndexIsSuccessThenGetIndex() {
        when(postService.findAll()).thenReturn(List.of(new Post(), new Post()));
        List<Post> res = postService.findAll();

        var model = new ConcurrentModel();
        var view = mainController.index(model);

        assertThat(view).isEqualTo("index");
        assertThat(model.getAttribute("posts")).isEqualTo(res);
        assertThat(res).isNotEmpty();
        assertThat(res).hasSize(2);
    }

    @Test
    void whenGetIndexIsFailedThenGetExceptionAndError() {
        when(postService.findAll()).thenThrow(new NotFoundException("Ошибка"));

        var model = new ConcurrentModel();
        var view = mainController.index(model);

        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("error")).isEqualTo(EXCEPTION_TEXT);
    }

    @Test
    void whenGetPostIsSuccessThenGetPost() {
        when(postService.findById(any())).thenReturn(new Post());
        Post res = postService.findById(any());
        HttpSession session = mock(HttpSession.class);

        var model = new ConcurrentModel();
        var view = mainController.post(1, model, session);

        assertThat(view).isEqualTo("view-post");
    }

    @Test
    void whenGetPostIsFailedThenGetExceptionAndError() {
        when(postService.findById(any())).thenThrow(new NotFoundException("Пост не был найден"));
        HttpSession session = mock(HttpSession.class);

        var model = new ConcurrentModel();
        var view = mainController.post(1, model, session);

        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("error")).isEqualTo("Пост не был найден");
    }

    @Test
    void whenGetPostIsFailedGlobalExceptionThenGetExceptionAndError() {
        when(postService.findById(any())).thenThrow(new RuntimeException("Ошибка"));
        HttpSession session = mock(HttpSession.class);

        var model = new ConcurrentModel();
        var view = mainController.post(1, model, session);

        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("error")).isEqualTo(EXCEPTION_TEXT);
    }

    @Test
    void whenGetCreatingPageIsSuccessThenGetCreatingPage() {
        when(markRepository.findAllOrderByNameAsc()).thenReturn(List.of(new Mark()));
        when(modelRepository.findAllOrderByNameAsc()).thenReturn(List.of(new Model()));
        when(engineRepository.findAllOrderById()).thenReturn(List.of(new Engine()));

        var model = new ConcurrentModel();
        var view = mainController.getCreatingPage(model);

        assertThat(view).isEqualTo("post-create");
        assertThat(model.getAttribute("marks")).isNotNull();
        assertThat(model.getAttribute("models")).isNotNull();
        assertThat(model.getAttribute("engines")).isNotNull();
    }

    @Test
    void whenGetCreatingPageIsFailedByMarkRepositoryThenGetExceptionAndError() {
        when(markRepository.findAllOrderByNameAsc()).thenThrow(new RuntimeException("ошибка"));
        when(modelRepository.findAllOrderByNameAsc()).thenReturn(List.of(new Model()));
        when(engineRepository.findAllOrderById()).thenReturn(List.of(new Engine()));

        var model = new ConcurrentModel();
        var view = mainController.getCreatingPage(model);

        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("error")).isEqualTo(EXCEPTION_TEXT);
    }

    @Test
    void whenGetCreatingPageIsFailedByModelRepositoryThenGetExceptionAndError() {
        when(markRepository.findAllOrderByNameAsc()).thenReturn(List.of(new Mark()));
        when(modelRepository.findAllOrderByNameAsc()).thenThrow(new RuntimeException("ошибка"));
        when(engineRepository.findAllOrderById()).thenReturn(List.of(new Engine()));

        var model = new ConcurrentModel();
        var view = mainController.getCreatingPage(model);

        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("error")).isEqualTo(EXCEPTION_TEXT);
    }

    @Test
    void whenGetCreatingPageIsFailedByEngineRepositoryThenGetExceptionAndError() {
        when(markRepository.findAllOrderByNameAsc()).thenReturn(List.of(new Mark()));
        when(modelRepository.findAllOrderByNameAsc()).thenReturn(List.of(new Model()));
        when(engineRepository.findAllOrderById()).thenThrow(new RuntimeException("ошибка"));

        var model = new ConcurrentModel();
        var view = mainController.getCreatingPage(model);

        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("error")).isEqualTo(EXCEPTION_TEXT);
    }
}