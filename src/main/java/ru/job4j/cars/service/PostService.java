package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.exception.NotFoundException;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.CarRepository;
import ru.job4j.cars.repository.PostRepository;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class PostService implements IService<Post, Integer> {
    private final PostRepository postRepository;
    private final CarRepository carRepository;

    @Override
    public Post save(Post post) {
        Post result = postRepository.create(post);
        if (result == null) {
            throw new NotFoundException("Не получилось сохранить пост");
        }
        return result;
    }

    @Override
    public boolean update(Post post, int id) {
        try {
            post.setId(id);
            Post postToTime = findById(id);
            Car oldCar = postToTime.getCar();
            post.setCreated(postToTime.getCreated());
            postRepository.update(post);

            if (oldCar != null && !Objects.equals(oldCar.getId(), post.getCar().getId())) {
                carRepository.delete(oldCar.getId());
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void delete(Integer id) {
        postRepository.delete(id);
    }

    @Override
    public Post findById(Integer id) {
        var post = postRepository.findPostById(id);
        if (post.isEmpty()) {
            throw new NotFoundException("Пост не был найден");
        }
        return post.get();
    }

    @Override
    public List<Post> findAll() {
        try {
            return postRepository.findAllOrderByTimeDesc();
        } catch (Exception e) {
            throw new NotFoundException("Произошла ошибка при поиске постов");
        }
    }
}
