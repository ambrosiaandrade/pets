package com.ambrosiaandrade.pets.interfaces;

import com.ambrosiaandrade.pets.entities.AnimalEntity;
import com.ambrosiaandrade.pets.models.Animal;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IAnimalMapper {

    AnimalEntity toEntity(Animal animal);
    Animal toModel(AnimalEntity animalEntity);

}
