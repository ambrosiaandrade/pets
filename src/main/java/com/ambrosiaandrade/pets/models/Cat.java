package com.ambrosiaandrade.pets.models;

import com.ambrosiaandrade.pets.enums.AnimalDietEnum;
import com.ambrosiaandrade.pets.enums.AnimalGenderEnum;
import com.ambrosiaandrade.pets.enums.AnimalTypeEnum;
import com.ambrosiaandrade.pets.interfaces.IAnimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.Period;

/**
 * Represents a Cat, which is a type of Animal.
 * It implements the IAnimal interface and provides specific behaviors for cats.
 * The age calculation is based on the cat's birthday.
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class Cat extends Animal implements IAnimal {

    public Cat() {
        this.setDiet(AnimalDietEnum.CARNIVOROUS);
        this.setType(AnimalTypeEnum.CAT);
        this.setGender(AnimalGenderEnum.UNKNOWN);
        this.setBirthday(LocalDate.now());
        this.calculateAge();
    }

    public Cat(LocalDate birthday) {
        this.setDiet(AnimalDietEnum.CARNIVOROUS);
        this.setType(AnimalTypeEnum.CAT);
        this.setGender(AnimalGenderEnum.UNKNOWN);
        this.setBirthday(birthday);
        this.calculateAge();
    }

    @Override
    public void makeSound() {
        log.info("Miau!");
    }

    @Override
    public void calculateAge() {
        var years = Period.between(getBirthday(), LocalDate.now()).getYears();
        var animalYears = 0;

        for (int i = 0; i < years; i++) {
            if (i == 0) {
                animalYears += 15; // first year
            } else if (i == 1) {
                animalYears += 9; // second year
            } else {
                animalYears += 4; // each subsequent year
            }
        }

        this.setAge(years);
        this.setAgeInHumanYears(animalYears);
    }

    // Overloaded method to sleep with no parameters
    public void sleep() {
        log.info(getName() + " is sleeping.");
    }
    
    // Overloaded method to sleep with a specified number of times
    public void sleep(int num) {
        log.info(getName() + " is sleeping.");
        for (int i = 0; i < num; i++) {
            log.info("Sleeping... " + (i + 1));
        }
    }

}
