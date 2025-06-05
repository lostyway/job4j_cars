package ru.job4j.cars.controller;

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
import ru.job4j.cars.dto.PhotoDto;
import ru.job4j.cars.exception.NotFoundException;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.*;
import ru.job4j.cars.service.*;

import java.io.IOException;
import java.time.Year;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("posts", postService.findAll());
        return "index";
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

        return "post-create";
    }

    @PostMapping("/post/save")
    public String createPost(@ModelAttribute Post post, @RequestParam("photo") List<MultipartFile> photos, Model model, HttpSession session) {
        try {
            saveCar(post);
            saveAuthor(post, session);
            Post postResult = postService.save(post);
            savePriceHistory(postResult, post.getPrice());
            savePhotos(postResult, photos);
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
            Post oldPost = postService.findById(post.getId());
            if (!checkOldPriceEqualsNew(oldPost.getPrice(), post.getPrice())) {
                savePriceHistory(post, post.getPrice());
            }
            checkPhotosInPost(post, photos);
            saveCar(post);
            saveAuthor(post, session);

            postService.update(post, post.getId());
            carService.delete(oldPost.getCar().getId());
            return "redirect:/post/" + post.getId();
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
            Set<Photo> photos = currentPost.getPhotos();
            if (photos != null && !photos.isEmpty()) {
                for (Photo photo : photos) {
                    photoService.deleteById(photo.getId());
                }
            }
            postService.delete(id);
            Car car = currentPost.getCar();
            carService.delete(car.getId());
            System.out.println("Car id is: " + car.getId());

            return "redirect:/";
        } catch (NotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "errors/404";
        } catch (Exception e) {
            model.addAttribute("error", "Произошла непредвиденная ошибка");
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

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                photoService.save(photoService.convertToPhotoDto(file), post);
                return ResponseEntity.ok("Файлы загружены");
            }
        }
        return ResponseEntity.ok("Файлы загружены");
    }

    private void saveAuthor(Post post, HttpSession session) {
        User user = (User) session.getAttribute("user");
        post.setAuthor(userService.findById(user.getId()));
    }

    private void saveCar(Post post) {
        Car car = carService.save(post.getCar());
        post.setCar(car);
    }

    private void savePhotos(Post post, List<MultipartFile> photos) throws IOException {
        for (MultipartFile photo : photos) {
            if (!photo.isEmpty()) {
                PhotoDto photoDto = new PhotoDto(photo.getOriginalFilename(), photo.getBytes());
                Photo savedPhoto = photoService.save(photoDto, post);
                post.getPhotos().add(savedPhoto);
            }
        }
    }

    private boolean isOwner(HttpSession session, Post post) {
        User user = (User) session.getAttribute("user");
        return user != null && post.getAuthor().getId() == user.getId();
    }

    private boolean checkOldPriceEqualsNew(long oldPrice, long newPrice) {
        return oldPrice == newPrice;
    }

    private void checkPhotosInPost(Post post, List<MultipartFile> photos) {
        try {
            Post oldPost = postService.findById(post.getId());
            Set<Photo> oldPhotos = oldPost.getPhotos();
            boolean hasNewPhotos = photos != null && photos.stream().anyMatch(p -> !p.isEmpty());
            if (hasNewPhotos) {
                for (Photo oldPhoto : oldPhotos) {
                    photoService.deleteById(oldPhoto.getId());
                }
                savePhotos(post, photos);
            } else {
                post.setPhotos(oldPhotos);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
