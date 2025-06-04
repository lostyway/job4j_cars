package ru.job4j.cars.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auto_post")
@EqualsAndHashCode(of = "id")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;

    private LocalDateTime created;

    @Column(nullable = false)
    private long price;

    @Column(name = "is_sold")
    private boolean isSold;

    @ManyToMany
    @JoinTable(name = "post_photos",
    joinColumns = {@JoinColumn(name = "post_id")},
    inverseJoinColumns = {@JoinColumn(name = "photo_id")})
    private Set<Photo> photos = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "auto_user_id")
    private User author;

    @OneToMany
    @JoinColumn(name = "auto_post_id")
    private List<PriceHistory> priceHistory = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "participates",
            joinColumns = {@JoinColumn(name = "post_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private List<User> subscribers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;
}
