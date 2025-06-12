package com.ambrosiaandrade.pets.service;

import com.ambrosiaandrade.pets.entities.AnimalEntity;
import com.ambrosiaandrade.pets.enums.AnimalTypeEnum;
import com.ambrosiaandrade.pets.exceptions.BaseException;
import com.ambrosiaandrade.pets.interfaces.IAnimalMapper;
import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.models.Cat;
import com.ambrosiaandrade.pets.models.Dog;
import com.ambrosiaandrade.pets.repositories.AnimalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final IAnimalMapper animalMapper;

    /**
     * Dependency injection using constructor is preferred for better testability and immutability.
     * An alternative, but not recommended, would be by attribute with @Autowired annotation,
     * */
    public AnimalService(AnimalRepository animalRepository, IAnimalMapper mapper) {
        this.animalRepository = animalRepository;
        this.animalMapper = mapper;
    }

    public Animal saveAnimal(Animal animal) {
        try {
            handleEmptyFields(animal);
            AnimalEntity entity = animalMapper.toEntity(animal);
            var savedAnimal = animalRepository.save(entity);
            return animalMapper.toModel(savedAnimal);
        } catch (DataAccessException e) {
            StackTraceElement stackTraceElement = e.getStackTrace()[0];
            log.error(stackTraceElement.toString());
            throw new BaseException(e.getMessage(), 500);
        }
    }

    public Animal getAnimal(int id) {
        try {
            Optional<AnimalEntity> optionalAnimal = animalRepository.findById(id);
            return optionalAnimal.map(animalMapper::toModel).orElseThrow(() -> new BaseException("Not found animal with " + id + " id", 404));
        } catch (DataAccessException e) {
            StackTraceElement stackTraceElement = e.getStackTrace()[0];
            log.error(stackTraceElement.toString());
            throw new BaseException(e.getMessage(), 500);
        }
    }

    public List<Animal> getAnimals() {
        try {
            var list = animalRepository.findAll();
            List<Animal> animals = new ArrayList<>();
            list.forEach(animalEntity -> animals.add(animalMapper.toModel(animalEntity)));
            return animals;
        } catch (DataAccessException e) {
            StackTraceElement stackTraceElement = e.getStackTrace()[0];
            log.error(stackTraceElement.toString());
            throw new BaseException(e.getMessage(), 500);
        }
    }

    public List<Animal> getAnimalsByType(String type) {
        try {
            AnimalTypeEnum animalType = AnimalTypeEnum.valueOf(type.toUpperCase());
            List<AnimalEntity> list = animalRepository.findByAnimalType(animalType);
            return list.stream().map(animalMapper::toModel).toList();
        } catch (IllegalArgumentException e) {
            log.error("Invalid animal type: " + type);
            throw new BaseException("Invalid animal type: " + type, 400);
        } catch (DataAccessException e) {
            StackTraceElement stackTraceElement = e.getStackTrace()[0];
            log.error(stackTraceElement.toString());
            throw new BaseException(e.getMessage(), 500);
        }
    }

    public void deleteAnimal(int id) {
        try {
            animalRepository.deleteById(id);
        } catch (DataAccessException e) {
            StackTraceElement stackTraceElement = e.getStackTrace()[0];
            log.error(stackTraceElement.toString());
            throw new BaseException(e.getMessage(), 500);
        }
    }

    public Animal updateAnimal(Animal animal, int id) {
        try {
            var animalUpdated = animalRepository.findById(id)
                .map(existing -> {
                    updateEntityFields(existing, animal);
                    return existing;
                })
                .orElseThrow(() -> new BaseException("Animal with ID " + id + " not found.", 404));
            return animalMapper.toModel(animalRepository.save(animalUpdated));
        } catch (DataAccessException e) {
            StackTraceElement stackTraceElement = e.getStackTrace()[0];
            log.error(stackTraceElement.toString());
            throw new BaseException(e.getMessage(), 500);
        }
    }

    private void handleEmptyFields(Animal animal) {
        Animal newAnimal = handleAnimalType(animal.getAnimalType(), animal.getBirthday());

        animal.setAge(newAnimal.getAge());
        animal.setAgeInHumanYears(newAnimal.getAgeInHumanYears());
        animal.setAnimalDiet(newAnimal.getAnimalDiet());
    }

    private void updateEntityFields(AnimalEntity existing, Animal newAnimal) {
        existing.setName(newAnimal.getName());
        existing.setBirthday(newAnimal.getBirthday());
        existing.setAnimalType(newAnimal.getAnimalType());

        newAnimal.calculateAge();
        existing.setAge(newAnimal.getAge());
        existing.setAgeInHumanYears(newAnimal.getAgeInHumanYears());
        existing.setAnimalDiet(newAnimal.getAnimalDiet());
    }

    private Animal handleAnimalType(AnimalTypeEnum animalType, LocalDate date) {
        return switch (animalType) {
            case CAT -> new Cat(date);
            case DOG -> new Dog(date);
            default -> new Animal(date);
        };
    }

}
