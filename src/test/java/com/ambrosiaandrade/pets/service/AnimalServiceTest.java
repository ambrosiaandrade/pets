package com.ambrosiaandrade.pets.service;

import com.ambrosiaandrade.pets.entities.AnimalEntity;
import com.ambrosiaandrade.pets.exceptions.BaseException;
import com.ambrosiaandrade.pets.factory.MockAnimal;
import com.ambrosiaandrade.pets.interfaces.IAnimalMapper;
import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.models.Cat;
import com.ambrosiaandrade.pets.repositories.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ambrosiaandrade.pets.enums.AnimalTypeEnum.CAT;
import static com.ambrosiaandrade.pets.enums.AnimalTypeEnum.DOG;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    private IAnimalMapper animalMapper;

    @InjectMocks
    private AnimalService animalService;

    @BeforeEach
    void setUp() {
        animalMapper = Mappers.getMapper(IAnimalMapper.class);
        MockitoAnnotations.openMocks(this);
        animalService = new AnimalService(animalRepository, animalMapper);
    }

    @Nested
    class SaveAnimal {

        @Test
        @DisplayName("Save animal - success")
        void saveAnimal_success() {
            var animals = MockAnimal.generateAnimals();

            for (Animal animal : animals) {
                AnimalEntity entity = animalMapper.toEntity(animal);
                entity.setId(1);

                when(animalRepository.save(entity)).thenReturn(entity);

                animal = animalMapper.toModel(entity);

                Animal result = animalService.saveAnimal(animal);

                assertNotNull(result);
                assertNotNull(result.getName());
                verify(animalRepository).save(entity);
            }

        }

        @Test
        @DisplayName("Save animal - error")
        void saveAnimal_dataAccessException() {
            Animal animal = MockAnimal.generateAnimal(DOG);
            AnimalEntity entity = animalMapper.toEntity(animal);

            when(animalRepository.save(entity)).thenThrow(new DataAccessException("DB error") {});

            Executable executable = () -> animalService.saveAnimal(animal);

            assertThrows(BaseException.class, executable);
        }

    }

    @Nested
    class GetAnimal {

        @Test
        @DisplayName("Get animal - success")
        void getAnimal_success() {
            AnimalEntity entity = animalMapper.toEntity(MockAnimal.generateAnimal(DOG));

            when(animalRepository.findById(anyInt())).thenReturn(Optional.of(entity));

            Animal result = animalService.getAnimal(1);

            assertNotNull(result);
            verify(animalRepository).findById(any());
        }

        @Test
        @DisplayName("Get animal - not found")
        void getAnimal_notFound() {
            when(animalRepository.findById(anyInt())).thenReturn(Optional.empty());

            Executable executable = () -> animalService.getAnimal(1);

            assertThrows(BaseException.class, executable);
            verify(animalRepository).findById(any());
        }

        @Test
        @DisplayName("Get animal - error")
        void getAnimal_dataAccessException() {
            when(animalRepository.findById(anyInt())).thenThrow(new DataAccessException("DB error") {});

            Executable executable = () -> animalService.getAnimal(1);

            assertThrows(BaseException.class, executable);
            verify(animalRepository).findById(any());
        }

        @Test
        @DisplayName("Get all animals - success")
        void getAnimals_success() {

            List<AnimalEntity> list = new ArrayList<>();

            var animals = MockAnimal.generateAnimals();
            animals.forEach(animal -> {
                list.add(animalMapper.toEntity(animal));
            });

            when(animalRepository.findAll()).thenReturn(list);

            List<Animal> result = animalService.getAnimals();

            assertNotNull(result);

            verify(animalRepository).findAll();
        }

        @Test
        @DisplayName("Get all animals - error")
        void getAnimals_dataAccessException() {
            when(animalRepository.findAll()).thenThrow(new DataAccessException("DB error") {});

            Executable executable = () -> animalService.getAnimals();

            assertThrows(BaseException.class, executable);
            verify(animalRepository).findAll();
        }

        @Test
        @DisplayName("Get animals by type - success")
        void getAnimalsByType_success() {

            List<AnimalEntity> list = List.of(animalMapper.toEntity(MockAnimal.generateAnimal(CAT)));

            when(animalRepository.findByAnimalType(any())).thenReturn(list);

            List<Animal> result = animalService.getAnimalsByType(CAT.name());

            assertNotNull(result);

            verify(animalRepository).findByAnimalType(any());
        }

        @Test
        @DisplayName("Get animals by type - error database")
        void getAnimalsByType_dataAccessException() {
            when(animalRepository.findByAnimalType(any())).thenThrow(new DataAccessException("DB error") {});

            Executable executable = () -> animalService.getAnimalsByType(CAT.name());

            assertThrows(BaseException.class, executable);
            verify(animalRepository).findByAnimalType(any());
        }

        @Test
        @DisplayName("Get animals by type - error illegalArgument")
        void getAnimalsByType_illegalArgumentException() {
            //when(animalRepository.findByAnimalType(any())).thenThrow(new IllegalArgumentException("Invalid animal type") {});

            Executable executable = () -> animalService.getAnimalsByType("INVALID_TYPE");

            assertThrows(BaseException.class, executable);
        }

    }

    @Nested
    class DeleteAnimal {

        @Test
        @DisplayName("Delete animal - success")
        void deleteAnimal_success() {
            animalService.deleteAnimal(1);
            verify(animalRepository).deleteById(1);
        }

        @Test
        @DisplayName("Delete animal - error dataAccessException")
        void deleteAnimal_dataAccessException() {
            doThrow(new DataAccessException("Error"){}).when(animalRepository).deleteById(anyInt());

            Executable executable = () -> animalService.deleteAnimal(1);

            assertThrows(BaseException.class, executable);
            verify(animalRepository).deleteById(1);
        }

    }

    @Nested
    class UpdateAnimal {

        @Test
        @DisplayName("Update animal - success")
        void updateAnimal_success() {
            Animal animal = MockAnimal.generateAnimal(DOG);
            AnimalEntity entity = animalMapper.toEntity(new Cat());

            when(animalRepository.findById(any())).thenReturn(Optional.of(entity));
            when(animalRepository.save(any())).thenReturn(animalMapper.toEntity(animal));

            var result = animalService.updateAnimal(animal, 1);

            assertNotNull(result);
            assertEquals(DOG.name(), result.getAnimalType().name());
        }

        @Test
        @DisplayName("Update animal - not found")
        void updateAnimal_notFound() {
            Animal animal = MockAnimal.generateAnimal(DOG);

            when(animalRepository.findById(any())).thenReturn(Optional.empty());

            Executable executable = () -> animalService.updateAnimal(animal, 1);

            assertThrows(BaseException.class, executable);
        }

        @Test
        @DisplayName("Update animal - dataAccessException")
        void updateAnimal_dataAccessException() {
            Animal animal = MockAnimal.generateAnimal(DOG);

            when(animalRepository.findById(any())).thenThrow(new DataAccessException("error") {});

            Executable executable = () -> animalService.updateAnimal(animal, 1);

            assertThrows(BaseException.class, executable);
        }

    }


}