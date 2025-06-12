package com.ambrosiaandrade.pets.service;

import com.ambrosiaandrade.pets.entities.AnimalEntity;
import com.ambrosiaandrade.pets.enums.AnimalDietEnum;
import com.ambrosiaandrade.pets.enums.AnimalTypeEnum;
import com.ambrosiaandrade.pets.exceptions.BaseException;
import com.ambrosiaandrade.pets.interfaces.IAnimalMapper;
import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.models.Cat;
import com.ambrosiaandrade.pets.repositories.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataAccessException;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private IAnimalMapper animalMapper;

    @InjectMocks
    private AnimalService animalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        animalService = new AnimalService(animalRepository, animalMapper);
    }

    @Test
    void saveAnimal_success() {
        Animal animal = new Animal();
        animal.setName("Tom");
        animal.setAnimalType(AnimalTypeEnum.CAT);
        AnimalEntity entity = new AnimalEntity();
        AnimalEntity savedEntity = new AnimalEntity();
        Animal mappedAnimal = new Animal();

        when(animalMapper.toEntity(animal)).thenReturn(entity);
        when(animalRepository.save(entity)).thenReturn(savedEntity);
        when(animalMapper.toModel(savedEntity)).thenReturn(mappedAnimal);

        Animal result = animalService.saveAnimal(animal);

        assertNotNull(result);
        verify(animalRepository).save(entity);
        verify(animalMapper).toModel(savedEntity);
    }

    @Test
    void saveAnimal_dataAccessException() {
        Animal animal = new Animal();
        animal.setName("Tom");
        AnimalEntity entity = new AnimalEntity();

        when(animalMapper.toEntity(animal)).thenReturn(entity);
        when(animalRepository.save(entity)).thenThrow(new DataAccessException("DB error") {});

        BaseException ex = assertThrows(BaseException.class, () -> animalService.saveAnimal(animal));
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    void getAnimal_found() {
        int id = 1;
        AnimalEntity entity = new AnimalEntity();
        Animal mappedAnimal = new Animal();

        when(animalRepository.findById(id)).thenReturn(Optional.of(entity));
        when(animalMapper.toModel(entity)).thenReturn(mappedAnimal);

        Animal result = animalService.getAnimal(id);

        assertNotNull(result);
        verify(animalRepository).findById(id);
    }

    @Test
    void getAnimal_notFound() {
        int id = 1;
        when(animalRepository.findById(id)).thenReturn(Optional.empty());

        BaseException ex = assertThrows(BaseException.class, () -> animalService.getAnimal(id));
        assertEquals(404, ex.getStatusCode());
    }

    @Test
    void getAnimal_dataAccessException() {
        int id = 1;
        when(animalRepository.findById(id)).thenThrow(new DataAccessException("DB error") {});

        BaseException ex = assertThrows(BaseException.class, () -> animalService.getAnimal(id));
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    void getAnimals_success() {
        List<AnimalEntity> entities = List.of(new AnimalEntity(), new AnimalEntity());
        when(animalRepository.findAll()).thenReturn(entities);
        when(animalMapper.toModel(any(AnimalEntity.class))).thenReturn(new Animal());

        List<Animal> result = animalService.getAnimals();

        assertEquals(2, result.size());
        verify(animalRepository).findAll();
    }

    @Test
    void getAnimals_dataAccessException() {
        when(animalRepository.findAll()).thenThrow(new DataAccessException("DB error") {});

        BaseException ex = assertThrows(BaseException.class, () -> animalService.getAnimals());
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    void getAnimalsByType_success() {
        String type = "cat";
        List<AnimalEntity> entities = List.of(new AnimalEntity());
        when(animalRepository.findByAnimalType(AnimalTypeEnum.CAT)).thenReturn(entities);
        when(animalMapper.toModel(any(AnimalEntity.class))).thenReturn(new Cat());

        List<Animal> result = animalService.getAnimalsByType(type);

        assertEquals(1, result.size());
        verify(animalRepository).findByAnimalType(AnimalTypeEnum.CAT);
    }

    @Test
    void getAnimalsByType_invalidType() {
        String type = "invalid";
        BaseException ex = assertThrows(BaseException.class, () -> animalService.getAnimalsByType(type));
        assertEquals(400, ex.getStatusCode());
    }

    @Test
    void getAnimalsByType_dataAccessException() {
        String type = "dog";
        when(animalRepository.findByAnimalType(AnimalTypeEnum.DOG)).thenThrow(new DataAccessException("DB error") {});

        BaseException ex = assertThrows(BaseException.class, () -> animalService.getAnimalsByType(type));
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    void deleteAnimal_success() {
        int id = 1;
        doNothing().when(animalRepository).deleteById(id);

        assertDoesNotThrow(() -> animalService.deleteAnimal(id));
        verify(animalRepository).deleteById(id);
    }

    @Test
    void deleteAnimal_dataAccessException() {
        int id = 1;
        doThrow(new DataAccessException("DB error") {}).when(animalRepository).deleteById(id);

        BaseException ex = assertThrows(BaseException.class, () -> animalService.deleteAnimal(id));
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    void updateAnimal_success() {
        int id = 1;
        AnimalEntity entity = new AnimalEntity();
        entity.setAnimalType(AnimalTypeEnum.CAT);
        Animal animal = new Animal();
        AnimalEntity savedEntity = new AnimalEntity();
        Animal mappedAnimal = new Animal();

        when(animalRepository.findById(id)).thenReturn(Optional.of(entity));
        when(animalRepository.save(entity)).thenReturn(savedEntity);
        when(animalMapper.toModel(savedEntity)).thenReturn(mappedAnimal);

        Animal result = animalService.updateAnimal(animal, id);

        assertNotNull(result);
        verify(animalRepository).save(entity);
    }

    @Test
    void updateAnimal_notFound() {
        int id = 1;
        Animal animal = new Animal();
        when(animalRepository.findById(id)).thenReturn(Optional.empty());

        BaseException ex = assertThrows(BaseException.class, () -> animalService.updateAnimal(animal, id));
        assertEquals(404, ex.getStatusCode());
    }

    @Test
    void updateAnimal_dataAccessException() {
        int id = 1;
        Animal animal = new Animal();
        when(animalRepository.findById(id)).thenThrow(new DataAccessException("DB error") {});

        BaseException ex = assertThrows(BaseException.class, () -> animalService.updateAnimal(animal, id));
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    void handleEmptyFields_setsDefaults() {
        Animal animal = new Animal();
        animal.setBirthday(LocalDate.now().minusYears(2));
        // animalType and animalDiet are null

        // handleEmptyFields is private, so test via saveAnimal
        AnimalEntity entity = new AnimalEntity();
        AnimalEntity savedEntity = new AnimalEntity();
        Animal mappedAnimal = new Animal();

        when(animalMapper.toEntity(any(Animal.class))).thenReturn(entity);
        when(animalRepository.save(entity)).thenReturn(savedEntity);
        when(animalMapper.toModel(savedEntity)).thenReturn(mappedAnimal);

        Animal result = animalService.saveAnimal(animal);

        assertNotNull(result);
        // Should have set default type and diet
        assertEquals(AnimalTypeEnum.OTHER, animal.getAnimalType());
        assertEquals(AnimalDietEnum.OMNIVOROUS, animal.getAnimalDiet());
    }
}