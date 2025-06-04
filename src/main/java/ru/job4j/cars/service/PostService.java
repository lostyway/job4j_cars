package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.exception.NotFoundException;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.PostRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class PostService implements IService<Post, Integer> {
    private final PostRepository postRepository;

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
            postRepository.update(post);
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
        var post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new NotFoundException("Пост не был найден");
        }
        return post.get();
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAllOrderById();
    }
}
