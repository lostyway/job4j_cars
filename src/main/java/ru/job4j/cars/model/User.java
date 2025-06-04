package ru.job4j.cars.model;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Builder
@Table(name = "auto_user")
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String login;
    private String password;
}
