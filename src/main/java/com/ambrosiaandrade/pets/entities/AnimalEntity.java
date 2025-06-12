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
    @Column(name = "animal_name", length = 50, nullable = false)
    private String name;
    @Column(name = "animal_age", length = 3)
    private int age;
    @Column(name = "age_in_human_years", length = 3)
    private int ageInHumanYears;
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(name = "animal_type", length = 20)
    private AnimalTypeEnum animalType;

    @Enumerated(EnumType.STRING)
    @Column(name = "animal_diet", length = 20)
    private AnimalDietEnum animalDiet;

    @Enumerated(EnumType.STRING)
    @Column(name = "animal_gender", length = 20)
    private AnimalGenderEnum animalGender;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

}
