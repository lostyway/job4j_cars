package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;

import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository extends AbstractCrudRepository<Post, Integer> {

    public PostRepository(SessionFactory sf) {
        super(sf, Post.class, "id");
    }

    public List<Post> findAllPostOrderById() {
        return txReturn(session -> session
                .createQuery("""
                                select distinct p from Post p
                                left join fetch p.photos
                                left join fetch p.author
                                left join fetch p.car
                                order by p.id
                                """
                        , Post.class)
                .list());
    }

    public Optional<Post> findPostById(int id) {
        return txReturn(session -> session
                .createQuery("""
                                select distinct p from Post p
                                left join fetch p.photos
                                left join fetch p.author
                                left join fetch p.car
                                left join fetch p.priceHistory ph
                                where p.id = :id
                                """
                        , Post.class)
                .setParameter("id", id)
                .uniqueResultOptional());
    }

    public List<Post> findAllOrderByTimeDesc() {
        return txReturn(session -> session
                .createQuery("""
                                select distinct p from Post p
                                left join fetch p.photos
                                left join fetch p.author
                                left join fetch p.car
                                order by p.created desc
                                """
                        , Post.class)
                .list());
    }
}
