package com.ambrosiaandrade.pets.service;

import com.ambrosiaandrade.pets.entities.AnimalEntity;
import com.ambrosiaandrade.pets.interfaces.IAnimalMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class AdvanceUtilTest {

    private AdvanceUtil advanceUtil;
    private IAnimalMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(IAnimalMapper.class);
        advanceUtil = new AdvanceUtil(mapper);
    }

    @Test
    void testGenerateAnimalsWithFor_ReturnsCorrectSize() {
        int number = 5;
        List<AnimalEntity> result = advanceUtil.generateAnimalsWithFor(number);

        assertNotNull(result);
        assertEquals(number + 1, result.size());
    }

    @Test
    void testGenerateAnimalWithIntStream_ReturnsCorrectSize() {
        int number = 3;
        List<AnimalEntity> result = advanceUtil.generateAnimalWithIntStream(number);

        assertNotNull(result);
        assertEquals(number + 1, result.size());
    }

    @Test
    void testGenerateAnimalWithIntStreamAndParallel_ReturnsCorrectSize() {
        int number = 7;
        List<AnimalEntity> result = advanceUtil.generateAnimalWithIntStreamAndParallel(number);

        assertNotNull(result);
        assertEquals(number + 1, result.size());
    }

    @Test
    void testGenerateAnimalsWithFor_Zero() {
        int number = 0;
        List<AnimalEntity> result = advanceUtil.generateAnimalsWithFor(number);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGenerateAnimalWithIntStream_Zero() {
        int number = 0;
        List<AnimalEntity> result = advanceUtil.generateAnimalWithIntStream(number);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGenerateAnimalWithIntStreamAndParallel_Zero() {
        int number = 0;
        List<AnimalEntity> result = advanceUtil.generateAnimalWithIntStreamAndParallel(number);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}