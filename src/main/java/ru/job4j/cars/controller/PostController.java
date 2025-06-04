package ru.job4j.cars.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.cars.exception.NotFoundException;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.*;
import ru.job4j.cars.service.CarService;
import ru.job4j.cars.service.PostService;
import ru.job4j.cars.service.UserService;

import java.time.Year;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PostController {
    private final CarService carService;
    private final PostRepository postRepository;
    private final MarkRepository markRepository;
    private final ModelRepository modelRepository;
    private final OwnerRepository ownerRepository;
    private final UserService userService;
    private final PostService postService;
    private final EngineRepository engineRepository;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("posts", postService.findAll());
        return "index";
    }

    @GetMapping("/post/{id}")
    public String post(@PathVariable int id, Model model) {
        try {
            Post post = postService.findById(id);
            model.addAttribute("post", post);
            return "view-post";
        } catch (NotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "errors/404";
        } catch (Exception e) {
            model.addAttribute("error", "Произошла непредвиденная ошибка");
            log.error(e.getMessage(), e);
            return "errors/404";
        }
    }

    @GetMapping("/post/create")
    public String getCreatingPage(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("marks", markRepository.findAllOrderByNameAsc());
        model.addAttribute("models", modelRepository.findAllOrderByNameAsc());
        model.addAttribute("engines", engineRepository.findAllOrderById());
        model.addAttribute("years", IntStream.rangeClosed(1940, Year.now().getValue())
                .boxed()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList()));
        model.addAttribute("users", userService.findAll());

        return "post-create";
    }

    @PostMapping("/post/save")
    public String createPost(@ModelAttribute Post post, Model model) {
        try {
            //TODO Сделать добавление фотографий
            //TODO Позже изменить на user из сессии (HttpSession).
            post.setAuthor(userService.findById(post.getAuthor().getId()));
            Car car = carService.save(post.getCar());
            post.setCar(car);
            Post postResult = postService.save(post);
            return "redirect:/post/" + postResult.getId();
        } catch (NotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "errors/404";
        } catch (Exception e) {
            model.addAttribute("error", "Произошла непредвиденная ошибка");
            log.error(e.getMessage(), e);
            return "errors/404";
        }
    }
}
