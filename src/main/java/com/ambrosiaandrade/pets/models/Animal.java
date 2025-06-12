package com.ambrosiaandrade.pets.models;

import com.ambrosiaandrade.pets.enums.AnimalDietEnum;
import com.ambrosiaandrade.pets.enums.AnimalGenderEnum;
import com.ambrosiaandrade.pets.enums.AnimalTypeEnum;

import com.ambrosiaandrade.pets.interfaces.IAnimal;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.Period;

/**
 * Animal class represents a generic animal with properties such as name, age,
 * age in human years, birthday, type, and diet.    
 * * It serves as a base class for specific animal types like Cat, Dog, etc.
 * * The class uses Lombok annotations for boilerplate code reduction.
 * */
@Data
@Slf4j
@AllArgsConstructor
public class Animal implements IAnimal {
    
    private int id;
    
    @NonNull
    private String name;
    private int age;
    private int ageInHumanYears;

    @NonNull
    private LocalDate birthday;

    @NonNull
    private AnimalTypeEnum animalType;
    private AnimalDietEnum animalDiet;
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
