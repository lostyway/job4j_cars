package ru.job4j.cars.controller;

import com.sun.xml.bind.v2.TODO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.cars.exception.NotFoundException;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Mark;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.*;
import ru.job4j.cars.service.CarService;
import ru.job4j.cars.service.PostService;

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
    private final UserRepository userRepository;
    private final PostService postService;
    private final EngineRepository engineRepository;

    @GetMapping("/")
    public String index() {
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
        model.addAttribute("users", userRepository.findAllOrderById());

        return "post-create";
    }

    @PostMapping("/post/save")
    public String createPost(Post post, Model model) {
        try {
            //TODO Сделать создание авто и поста человеческим.
            Car car = carService.save(post.getCar());
            var user = userRepository.findById(post.getAuthor().getId());
            post.setAuthor(user.get());
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
