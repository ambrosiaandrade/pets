package com.ambrosiaandrade.pets.models;

import com.ambrosiaandrade.pets.enums.AnimalDietEnum;
import com.ambrosiaandrade.pets.enums.AnimalGenderEnum;
import com.ambrosiaandrade.pets.enums.AnimalTypeEnum;
import com.ambrosiaandrade.pets.interfaces.IAnimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.Period;

/**
 * Animal class represents a generic animal with properties such as name, age,
 * age in human years, birthday, type, and diet.    
 * * It serves as a base class for specific animal types like Cat, Dog, etc.
 * * The class uses Lombok annotations for boilerplate code reduction.
 * */
@Schema(description = "Animal model representing a generic animal with properties such as name, age, birthday, type, and diet.")
@Data
@Slf4j
@AllArgsConstructor
public class Animal implements IAnimal {
    
    @Schema(description = "Unique identifier for the animal", example = "1")
    private int id;
    
    @Schema(description = "Name of the animal", example = "Buddy")
    @NonNull
    private String name;
    @Schema(description = "Age of the animal in years", example = "5")
    private int age;
    @Schema(description = "Age of the animal in human years", example = "35")
    private int ageInHumanYears;

    @NonNull
    @Schema(description = "Birthday of the animal in ISO format (YYYY-MM-DD)", example = "2018-05-20")
    private LocalDate birthday;

    @NonNull
    @Schema(description = "Type of the animal", example = "DOG")
    private AnimalTypeEnum animalType;
    @Schema(description = "Diet of the animal", example = "OMNIVOROUS")
    private AnimalDietEnum animalDiet;
    @Schema(description = "Gender of the animal", example = "FEMALE")
    private AnimalGenderEnum animalGender;

    public Animal () {
        this.setBirthday(LocalDate.now());
        calculateAge();
        this.setAnimalDiet(AnimalDietEnum.OMNIVOROUS);
        this.setAnimalType(AnimalTypeEnum.OTHER);
        this.setAnimalGender(AnimalGenderEnum.UNKNOWN);
    }

    public Animal (LocalDate date) {
        this.setBirthday(date);
        calculateAge(date);
        this.setAnimalDiet(AnimalDietEnum.OMNIVOROUS);
        this.setAnimalType(AnimalTypeEnum.OTHER);
        this.setAnimalGender(AnimalGenderEnum.UNKNOWN);
    }

    @Override
    public void makeSound() {
        log.info("...");
    }

    @Override
    public void calculateAge() {
        var years = Period.between(getBirthday(), LocalDate.now()).getYears();
        this.setAge(years);
        this.setAgeInHumanYears(years);
    }

    public void calculateAge(LocalDate date) {
        int years = Period.between(date, LocalDate.now()).getYears();
        this.setAge(years);
        this.setAgeInHumanYears(years);
    }

    public void setBirthday(LocalDate date) {
        this.birthday = date;
        calculateAge(date);
    }

}
