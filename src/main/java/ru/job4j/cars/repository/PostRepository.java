package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;

@Repository
public class PostRepository extends AbstractCrudRepository<Post, Integer>{

    public PostRepository(SessionFactory sf) {
        super(sf, Post.class, "id");
    }
}
