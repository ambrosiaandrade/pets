package com.ambrosiaandrade.pets.service;

import com.ambrosiaandrade.pets.entities.AnimalEntity;
import com.ambrosiaandrade.pets.interfaces.IAnimalMapper;
import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.models.Cat;
import com.ambrosiaandrade.pets.models.Dog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Component
public class AdvanceUtil {

    @Autowired
    private IAnimalMapper mapper;

    public List<AnimalEntity> generateAnimalsWithFor(int number) {
        var entities = new ArrayList<AnimalEntity>();
        for (int i = 0; i <= number; i++) {
            entities.add(mapper.toEntity(generateAnimal()));
        }
        return entities;
    }

    public List<AnimalEntity> generateAnimalWithIntStream(int number) {
        return IntStream.rangeClosed(0, number)
                .mapToObj(i -> mapper.toEntity(generateAnimalThreadSafe()))
                .toList();
    }

    public List<AnimalEntity> generateAnimalWithIntStreamAndParallel(int number) {
        return IntStream.rangeClosed(0, number)
                .parallel()
                .mapToObj(i -> mapper.toEntity(generateAnimalThreadSafe()))
                .toList();
    }

    private Animal generateAnimal() {
        Random random = new Random();
        Animal animal = getAnimal(random);
        animal.setName(getAnimalName(random));
        return animal;
    }

    private Animal generateAnimalThreadSafe() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Animal animal = getAnimal(random);
        animal.setName(getAnimalName(random));
        return animal;
    }

    // Java 21+, ThreadLocalRandom does not extends from Random
    private Animal getAnimal(Random random) {
        return switch (random.nextInt(2)) {
            case 0 -> new Cat();
            case 1 -> new Dog();
            default -> new Animal();
        };
    }

    private Animal getAnimal(ThreadLocalRandom random) {
        return switch (random.nextInt(2)) {
            case 0 -> new Cat();
            case 1 -> new Dog();
            default -> new Animal();
        };
    }

    private String getAnimalName(Random random) {
        List<String> names = Arrays.asList("Shadow", "Charlie", "Max", "Coco", "Bailey", "Lucky");
        return names.get(random.nextInt(names.size()));
    }

    private String getAnimalName(ThreadLocalRandom random) {
        List<String> names = Arrays.asList("Shadow", "Charlie", "Max", "Coco", "Bailey", "Lucky");
        return names.get(random.nextInt(names.size()));
    }

}
