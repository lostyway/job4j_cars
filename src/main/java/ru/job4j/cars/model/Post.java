package ru.job4j.cars.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auto_post")
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "description", "created", "price", "sold", "author", "car"})
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;

    @Column(updatable = false)
    private LocalDateTime created = LocalDateTime.now();

    @Column(nullable = false)
    private Long price;

    @Column(name = "is_sold")
    private boolean sold;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Photo> photos = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "auto_user_id")
    private User author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("created ASC")
    private Set<PriceHistory> priceHistory = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(
            name = "participates",
            joinColumns = {@JoinColumn(name = "post_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private List<User> subscribers = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "car_id")
    private Car car;

    public Post(int i, String desc1, LocalDateTime localDateTime, long price, boolean b, User user, Car car) {
        this.id = i;
        this.description = desc1;
        this.created = localDateTime;
        this.price = price;
        this.sold = b;
        this.author = user;
        this.car = car;
    }
}
