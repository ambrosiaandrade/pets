package com.ambrosiaandrade.pets.repositories;

import com.ambrosiaandrade.pets.entities.AnimalEntity;

import com.ambrosiaandrade.pets.enums.AnimalTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for managing AnimalEntity objects.
 * It extends CrudRepository to provide basic CRUD operations.
 */
@Repository
public interface AnimalRepository extends JpaRepository<AnimalEntity, Integer> {

    List<AnimalEntity> findByAnimalType(AnimalTypeEnum type);

}
