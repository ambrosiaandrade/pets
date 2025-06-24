package com.ambrosiaandrade.pets.entities;

import com.ambrosiaandrade.pets.enums.AnimalDietEnum;
import com.ambrosiaandrade.pets.enums.AnimalGenderEnum;
import com.ambrosiaandrade.pets.enums.AnimalTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

/**
 * Entity class representing an animal in the database.
 * It includes fields for the animal's ID, name, age, birthday,
 * type, diet, and creation timestamp.
 */
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "animal")
public class AnimalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(length = 50, nullable = false)
    private String name;
    @Column(length = 3)
    private int age;
    @Column(length = 3)
    private int ageInHumanYears;
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AnimalTypeEnum type;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AnimalDietEnum diet;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AnimalGenderEnum gender;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDate createdAt;

}
