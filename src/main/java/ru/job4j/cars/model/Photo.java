package ru.job4j.cars.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "photos")
@Data
@ToString
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String path;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public Photo(String name, String path, Post post) {
        this.name = name;
        this.path = path;
        this.post = post;
    }

    public Photo() {
    }
}
