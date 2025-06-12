package com.ambrosiaandrade.pets.models;

import java.time.LocalDate;
import java.time.Period;

import com.ambrosiaandrade.pets.enums.AnimalDietEnum;
import com.ambrosiaandrade.pets.enums.AnimalTypeEnum;
import com.ambrosiaandrade.pets.interfaces.IAnimal;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class Dog extends Animal implements IAnimal {

    public Dog() {
        this.setAnimalDiet(AnimalDietEnum.OMNIVOROUS);
        this.setAnimalType(AnimalTypeEnum.DOG);
    }

    public Dog(LocalDate birthday) {
        this.setAnimalDiet(AnimalDietEnum.OMNIVOROUS);
        this.setAnimalType(AnimalTypeEnum.DOG);
        this.setBirthday(birthday);
        this.calculateAge();
    }

    @Override
    public void makeSound() {
        log.info("Au au!");
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

    public void jumpAtOwners() {
        log.info(getName() + " is jumping at its owner!");
    }

}