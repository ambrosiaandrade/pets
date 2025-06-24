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

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class Dog extends Animal implements IAnimal {

    public Dog() {
        this.setDiet(AnimalDietEnum.OMNIVOROUS);
        this.setType(AnimalTypeEnum.DOG);
        this.setGender(AnimalGenderEnum.UNKNOWN);
        this.setBirthday(LocalDate.now());
        this.calculateAge();
    }

    public Dog(LocalDate birthday) {
        this.setDiet(AnimalDietEnum.OMNIVOROUS);
        this.setType(AnimalTypeEnum.DOG);
        this.setGender(AnimalGenderEnum.UNKNOWN);
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