package com.ambrosiaandrade.pets.service;

import com.ambrosiaandrade.pets.entities.AnimalEntity;
import com.ambrosiaandrade.pets.enums.AnimalTypeEnum;
import com.ambrosiaandrade.pets.factory.MockAnimal;
import com.ambrosiaandrade.pets.interfaces.IAnimalMapper;
import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.repositories.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvanceServiceTest {

    @Mock
    private AnimalRepository repository;

    private IAnimalMapper mapper;

    @Mock
    private AdvanceUtil util;

    @InjectMocks
    private AdvanceService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = Mappers.getMapper(IAnimalMapper.class);
        service = new AdvanceService(repository, mapper, util);
    }

    @Nested
    class Data {

        @Test
        void generateAnimalsAndSave_sucess() {
            int number = 2;
            List<AnimalEntity> forList = Arrays.asList(new AnimalEntity(), new AnimalEntity());
            List<AnimalEntity> intStreamList = Arrays.asList(new AnimalEntity());
            List<AnimalEntity> parallelList = Arrays.asList(new AnimalEntity(), new AnimalEntity(), new AnimalEntity());

            when(util.generateAnimalsWithFor(number)).thenReturn(forList);
            when(util.generateAnimalWithIntStream(number)).thenReturn(intStreamList);
            when(util.generateAnimalWithIntStreamAndParallel(number)).thenReturn(parallelList);

            service.generateAnimalsAndSave(number);

            verify(repository).saveAll(forList);
            verify(repository).saveAll(intStreamList);
            verify(repository).saveAll(parallelList);
        }

        @Test
        void generateAnimalsAndSave_error() {
            int number = 1;
            when(util.generateAnimalsWithFor(number)).thenThrow(new DataAccessException("DB error") {
            });

            service.generateAnimalsAndSave(number);

            verify(util).generateAnimalsWithFor(number);
            verify(repository, never()).saveAll(any());
        }

        @Test
        void getDataNoPagination() {
            AnimalEntity entity = getAnimalEntity();
            List<AnimalEntity> entities = Collections.singletonList(entity);
            when(repository.findAll()).thenReturn(entities);

            List<Animal> result = service.getDataNoPagination();

            assertEquals(1, result.size());
        }

        @Test
        void getDataNoPaginationButWithCache() {
            AnimalEntity entity = getAnimalEntity();
            List<AnimalEntity> entities = Collections.singletonList(entity);
            when(repository.findAll()).thenReturn(entities);

            List<Animal> result = service.getDataNoPaginationButWithCache();

            assertEquals(1, result.size());
        }

        @Test
        void getDataWithPagination() {
            Pageable pageable = mock(Pageable.class);
            AnimalEntity entity = getAnimalEntity();
            Page<AnimalEntity> page = new PageImpl<>(Collections.singletonList(entity));
            when(repository.findAll(pageable)).thenReturn(page);

            Page<Animal> result = service.getDataWithPagination(pageable);

            assertEquals(1, result.getTotalElements());
        }
    }

    private AnimalEntity getAnimalEntity() {
        return mapper.toEntity(MockAnimal.generateAnimal(AnimalTypeEnum.CAT));
    }
}
