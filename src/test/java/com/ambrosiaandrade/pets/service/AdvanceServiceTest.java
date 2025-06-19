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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
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
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        mapper = Mappers.getMapper(IAnimalMapper.class);
        service = new AdvanceService(repository, mapper, util);

        Field field = AdvanceService.class.getDeclaredField("animalLimit");
        field.setAccessible(true);
        field.set(service, 2000L);
    }

    @Nested
    class GenerateData {

        @ParameterizedTest
        @ValueSource(ints = {-1, 0, 2, 1000})
        void generateAnimalsAndSave_sucess(int number) {

            boolean invalidNumber = number <= 0 || number == 1000;

            if (invalidNumber) {
                when(repository.count()).thenReturn(2000L);
                var result = service.generateAnimalsAndSave(number);
                assertEquals(1, result.size());
                verify(repository, times(0)).saveAll(anyList());
            } else {
                when(repository.count()).thenReturn(100L);
                var result = service.generateAnimalsAndSave(number);
                assertEquals(3, result.size());
                verify(repository, times(3)).saveAll(anyList());
            }

        }

        @Test
        void generateAnimalsAndSave_error() {
            int number = 1;

            when(repository.count()).thenReturn(10L);

            when(util.generateAnimalsWithFor(number))
                    .thenThrow(new DataAccessException("DB error") {
                    });

            service.generateAnimalsAndSave(number);

            verify(util).generateAnimalsWithFor(number);
            verify(repository, never()).saveAll(any());
        }
    }

    @Nested
    class Pagination {

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
