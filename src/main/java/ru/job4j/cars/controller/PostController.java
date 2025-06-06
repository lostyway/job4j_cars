package ru.job4j.cars.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.GlobalExceptionMessage;
import ru.job4j.cars.dto.PhotoDto;
import ru.job4j.cars.exception.NotFoundException;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.EngineRepository;
import ru.job4j.cars.repository.MarkRepository;
import ru.job4j.cars.repository.ModelRepository;
import ru.job4j.cars.service.*;

import java.time.Year;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PostController {
    private final CarService carService;
    private final MarkRepository markRepository;
    private final ModelRepository modelRepository;
    private final UserService userService;
    private final PostService postService;
    private final EngineRepository engineRepository;
    private final PhotoService photoService;
    private final PriceService priceService;
    private static final String EXCEPTION = GlobalExceptionMessage.GLOBAL_EXCEPTION_MESSAGE.getMessage();

    @GetMapping("/")
    public String index(Model model) {
        try {
            model.addAttribute("posts", postService.findAll());
            return "index";
        } catch (Exception e) {
            log.error(EXCEPTION, e);
            model.addAttribute("error", EXCEPTION);
            return "errors/404";
        }
    }

    @GetMapping("/post/{id}")
    public String post(@PathVariable int id, Model model, HttpSession session) {
        try {
            Post post = postService.findById(id);
            model.addAttribute("post", post);

            boolean isAdmin = isOwner(session, post);
            model.addAttribute("isAdmin", isAdmin);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            String priceHistoryJson = mapper.writeValueAsString(post.getPriceHistory());
            model.addAttribute("priceHistoryJson", priceHistoryJson);

            return "view-post";
        } catch (NotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "errors/404";
        } catch (Exception e) {
            model.addAttribute("error", EXCEPTION);
            log.error(e.getMessage(), e);
            return "errors/404";
        }
    }

    @GetMapping("/post/create")
    public String getCreatingPage(Model model) {
        try {
            model.addAttribute("post", new Post());
            model.addAttribute("marks", markRepository.findAllOrderByNameAsc());
            model.addAttribute("models", modelRepository.findAllOrderByNameAsc());
            model.addAttribute("engines", engineRepository.findAllOrderById());
            model.addAttribute("years", IntStream.rangeClosed(1940, Year.now().getValue())
                    .boxed()
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList()));

            return "post-create";
        } catch (Exception e) {
            model.addAttribute("error", EXCEPTION);
            log.error(e.getMessage(), e);
            return "errors/404";
        }
    }

    @PostMapping("/post/save")
    public String createPost(@ModelAttribute Post post, @RequestParam("photo") List<MultipartFile> photos, Model model, HttpSession session) {
        try {
            saveAuthor(post, session);
            validatePost(post);
            postService.save(post);
            savePriceHistory(post, post.getPrice());
            photoService.savePhotos(post, photos);
            return "redirect:/post/" + post.getId();
        } catch (NotFoundException | IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "errors/404";
        } catch (Exception e) {
            model.addAttribute("error", EXCEPTION);
            log.error(e.getMessage(), e);
            return "errors/404";
        }
    }

    private void savePriceHistory(Post post, long price) {
        try {
            PriceHistory priceHistory = new PriceHistory();
            priceHistory.setPrice(price);
            priceHistory.setPost(post);
            priceService.save(priceHistory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/post/update/{id}")
    public String updateCurrentPost(@PathVariable int id, Model model, HttpSession session) {
        Post post = postService.findById(id);
        if (!isOwner(session, post)) {
            model.addAttribute("error", "Вы не являетесь создателем публикации");
            return "errors/404";
        }
        model.addAttribute("marks", markRepository.findAllOrderByNameAsc());
        model.addAttribute("models", modelRepository.findAllOrderByNameAsc());
        model.addAttribute("engines", engineRepository.findAllOrderById());
        model.addAttribute("years", IntStream.rangeClosed(1940, Year.now().getValue())
                .boxed()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList()));
        model.addAttribute("post", post);
        return "post-edit";
    }

    @PostMapping("/post/update")
    public String submitPostUpdate(@ModelAttribute Post post, @RequestParam("photo") List<MultipartFile> photos, Model model, HttpSession session) {
        try {
            Post postFromBase = postService.findById(post.getId());
            if (postFromBase == null || post == null) {
                model.addAttribute("error", "Пост пуст");
                return "errors/404";
            }
            if (!isOwner(session, postFromBase)) {
                model.addAttribute("error", "Вы не являетесь создателем публикации");
                return "errors/404";
            }
            Car oldCar = postFromBase.getCar();
            savePriceHistoryIfChanged(post, postFromBase);
            postFromBase.setDescription(post.getDescription());
            postFromBase.setPrice(post.getPrice());
            postFromBase.setSold(post.isSold());
            postFromBase.setCar(post.getCar());

            saveAuthor(postFromBase, session);
            validatePost(postFromBase);
            photoService.checkAndDeletePhotosInPost(photos, postFromBase);

            postService.update(postFromBase, postFromBase.getId());
            return "redirect:/post/" + postFromBase.getId();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "errors/404";
        }
    }

    @PostMapping("/post/delete/{id}")
    public String deletePost(@PathVariable int id, Model model) {
        try {
            Post currentPost = postService.findById(id);
            if (currentPost == null) {
                model.addAttribute("error", "Поста не существует");
                return "errors/404";
            }
            Car car = currentPost.getCar();
            photoService.deleteAllPhotosInPost(currentPost);
            postService.delete(id);
            carService.delete(car.getId());
            return "redirect:/";
        } catch (NotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "errors/404";
        } catch (
                Exception e) {
            model.addAttribute("error", EXCEPTION);
            log.error(e.getMessage(), e);
            return "errors/404";
        }
    }

    @GetMapping("/photo/{id}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable int id) {
        Optional<PhotoDto> photoOpt = photoService.getPhotoById(id);

        if (photoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PhotoDto dto = photoOpt.get();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + dto.getName())
                .contentType(MediaType.IMAGE_JPEG)
                .body(dto.getContent());
    }

    @PostMapping("/photo/upload")
    public ResponseEntity<?> uploadPhotos(@RequestParam("photo") MultipartFile[] files, @RequestParam("postId") int postId) {
        Post post = postService.findById(postId);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пост не найден");
        }

        boolean isLoaded = false;
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                photoService.save(photoService.convertToPhotoDto(file), post);
                isLoaded = true;
            }
        }

        return isLoaded ? ResponseEntity.ok("Файлы загружены")
                : ResponseEntity.badRequest().body("Файлы не были загружены");
    }

    private void saveAuthor(Post post, HttpSession session) {
        User user = (User) session.getAttribute("user");
        post.setAuthor(userService.findById(user.getId()));
    }

    private boolean isOwner(HttpSession session, Post post) {
        User user = (User) session.getAttribute("user");
        return user != null && post.getAuthor().getId() == user.getId();
    }

    private void savePriceHistoryIfChanged(Post newPost, Post postFromBase) {
        if (!checkOldPriceEqualsNew(postFromBase.getPrice(), newPost.getPrice())) {
            savePriceHistory(postFromBase, newPost.getPrice());
        }
    }

    private boolean checkOldPriceEqualsNew(long oldPrice, long newPrice) {
        return oldPrice == newPrice;
    }

    private void validatePost(Post post) {
        if (post == null || post.getPrice() == null || post.getPrice() < 0 || post.getPrice() > 1000000000 || post.getDescription() == null || post.getCar() == null || post.getAuthor() == null) {
            throw new IllegalArgumentException("Данные были заполнены неверно!");
        }
    }
}
