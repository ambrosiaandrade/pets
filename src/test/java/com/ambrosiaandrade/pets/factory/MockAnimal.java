package com.ambrosiaandrade.pets.factory;

import com.ambrosiaandrade.pets.enums.AnimalGenderEnum;
import com.ambrosiaandrade.pets.enums.AnimalTypeEnum;
import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.models.Cat;
import com.ambrosiaandrade.pets.models.Dog;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.ambrosiaandrade.pets.enums.AnimalTypeEnum.*;

public class MockAnimal {

    public static Animal generateAnimal(AnimalTypeEnum type) {

        Animal animal = switch (type) {
            case CAT -> new Cat();
            case DOG -> new Dog();
            default -> new Animal();
        };

        animal.setBirthday(LocalDate.of(2020,1,1));
        animal.setName("Andromache");
        animal.setGender(AnimalGenderEnum.FEMALE);
        animal.calculateAge();

        return animal;
    }

    public static List<Animal> generateAnimals() {

        List<Animal> animals = List.of(generateAnimal(CAT), generateAnimal(DOG), generateAnimal(OTHER));
        List<String> names = petNames();

        Random r = new Random();
        animals.forEach(animal -> {
            int randomIndex = r.nextInt(names.size());
            String randomName = names.get(randomIndex);
            animal.setName(randomName);
            AnimalGenderEnum[] genders = AnimalGenderEnum.values();
            randomIndex = r.nextInt(genders.length);
            animal.setGender(genders[randomIndex]);
        });

        return animals;
    }

    private static List<String> petNames() {
        return Arrays.asList(
                "Shadow",
                "Charlie",
                "Max",
                "Coco",
                "Bailey",
                "Lucky",
                "Sam",
                "Toby",
                "Sky",
                "Riley",
                "Sandy",
                "Ziggy",
                "Oreo",
                "Pumpkin",
                "Smokey",
                "Harley",
                "Scout",
                "Storm",
                "Blue",
                "Casey"
        );
    }

}
