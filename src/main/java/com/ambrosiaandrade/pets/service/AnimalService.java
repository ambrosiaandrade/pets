package com.ambrosiaandrade.pets.service;

import com.ambrosiaandrade.pets.entities.AnimalEntity;
import com.ambrosiaandrade.pets.enums.AnimalDietEnum;
import com.ambrosiaandrade.pets.enums.AnimalTypeEnum;
import com.ambrosiaandrade.pets.exceptions.BaseException;
import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.models.Cat;
import com.ambrosiaandrade.pets.models.Dog;
import com.ambrosiaandrade.pets.repositories.AnimalRepository;
import com.ambrosiaandrade.pets.interfaces.IAnimalMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final IAnimalMapper animalMapper;

    /**
     * Dependency injection using constructor.
     * An alternative, but not recommended, would be by attribute with @Autowired annotation,
     * as constructor injection is preferred for better testability and immutability.
     * */
    public AnimalService(AnimalRepository animalRepository, IAnimalMapper mapper) {
        this.animalRepository = animalRepository;
        this.animalMapper = mapper;
    }

    /**
     * Saves an animal to the database after handling empty fields and mapping the model to an entity.
     * If a database access error occurs, it logs the error and throws a BaseException with a 500 status code.
     */
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

    /**
     * Retrieves an animal by its ID from the database.
     * If the animal is not found, it throws a BaseException with a 404 status code.
     * If a database access error occurs, it logs the error and throws a BaseException with a 500 status code.
     */
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

    /**
     * Retrieves all animals from the database.
     * If a database access error occurs, it logs the error and throws a BaseException with a 500 status code.
     */
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

    /**
     * Retrieves animals by their type from the database.
     * If the type is invalid, it throws a BaseException with a 400 status code.
     * If a database access error occurs, it logs the error and throws a BaseException with a 500 status code.
     * Returns a list of Animal models filtered by the specified type.
     */
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

    /**
     * Deletes an animal by its ID from the database.
     * If a database access error occurs, it logs the error and throws a BaseException with a 500 status code.
     */
    public void deleteAnimal(int id) {
        try {
            animalRepository.deleteById(id);
        } catch (DataAccessException e) {
            StackTraceElement stackTraceElement = e.getStackTrace()[0];
            log.error(stackTraceElement.toString());
            throw new BaseException(e.getMessage(), 500);
        }
    }

    /**
     * Updates an existing animal in the database.
     * If the animal with the specified ID does not exist, it throws a BaseException with a 404 status code.
     * If a database access error occurs, it logs the error and throws a BaseException with a 500 status code.
     *
     * @return
     */
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

    /**
     * Handles empty fields in the Animal model by setting default values.
     * If the animal type is null, it sets it to OTHER.
     * If the animal diet is null, it sets it to OMNIVOROUS.
     * 
     * @param animal The Animal model to be checked and modified.
     * @return The modified Animal model with default values set for empty fields.
     */
    private Animal handleEmptyFields(Animal animal) {
        if (animal.getAnimalType() == null)
            animal.setAnimalType(AnimalTypeEnum.OTHER);
        if (animal.getAnimalDiet() == null)
            animal.setAnimalDiet(AnimalDietEnum.OMNIVOROUS);
        if (animal.getBirthday() != null) {
            var years = Period.between(animal.getBirthday(), LocalDate.now()).getYears();
            animal.setAge(years);
        }
        Animal newAnimal = handleAnimalType(animal.getAnimalType(), animal.getBirthday());

        animal.setAge(newAnimal.getAge());
        animal.setAgeInHumanYears(newAnimal.getAgeInHumanYears());
        animal.setAnimalDiet(newAnimal.getAnimalDiet());

        return animal;
    }

    /**
     * Updates the fields of an existing AnimalEntity with values from the provided Animal model.
     * This method is used to update the entity in the database with new values.
     *
     * @param e The existing AnimalEntity to be updated.
     * @param a The Animal model containing new values.
     */
    private void updateEntityFields(AnimalEntity e, Animal a) {
        if (a.getName() != null && !a.getName().isBlank()) {
            e.setName(a.getName());
        }

        if (a.getAge() > 0) {
            e.setAge(a.getAge());
        }

        if (a.getBirthday() != null) {
            e.setBirthday(a.getBirthday());
        }

        if (a.getAnimalType() != null && !AnimalTypeEnum.OTHER.equals(a.getAnimalType())) {
            e.setAnimalType(a.getAnimalType());
        }

        Animal newAnimal = handleAnimalType(e.getAnimalType(), e.getBirthday());

        e.setAge(newAnimal.getAge());
        e.setAgeInHumanYears(newAnimal.getAgeInHumanYears());
        e.setAnimalDiet(newAnimal.getAnimalDiet());

    }

    private Animal handleAnimalType(AnimalTypeEnum animalType, LocalDate date) {
        return switch (animalType) {
            case CAT -> new Cat(date);
            case DOG -> new Dog(date);
            default -> new Animal();
        };
    }

}
