package com.ambrosiaandrade.pets.models;

import com.ambrosiaandrade.pets.enums.AnimalDietEnum;
import com.ambrosiaandrade.pets.enums.AnimalTypeEnum;

import lombok.*;

import java.time.LocalDate;

/**
 * Animal class represents a generic animal with properties such as name, age,
 * age in human years, birthday, type, and diet.    
 * * It serves as a base class for specific animal types like Cat, Dog, etc.
 * * The class uses Lombok annotations for boilerplate code reduction.
 * */
@Data
@AllArgsConstructor
public class Animal {
    
    private int id;
    
    @NonNull
    private String name;
    private int age;
    private int ageInHumanYears;

    @NonNull
    private LocalDate birthday;
    private AnimalTypeEnum animalType;
    private AnimalDietEnum animalDiet;

    public Animal () {
        this.setBirthday(LocalDate.now());
        this.setAge(0);
        this.setAgeInHumanYears(0);
        this.setAnimalDiet(AnimalDietEnum.OMNIVOROUS);
        this.setAnimalType(AnimalTypeEnum.OTHER);
    }

}
