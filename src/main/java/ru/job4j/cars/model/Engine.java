package ru.job4j.cars.model;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Builder
@Table(name = "engines")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Engine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
}
