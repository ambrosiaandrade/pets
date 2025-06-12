package com.ambrosiaandrade.pets.controller;

import static com.ambrosiaandrade.pets.enums.AnimalTypeEnum.DOG;
import static com.ambrosiaandrade.pets.enums.AnimalTypeEnum.OTHER;
import static com.ambrosiaandrade.pets.enums.AnimalTypeEnum.CAT;

import com.ambrosiaandrade.pets.interfaces.IAnimalMapper;
import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.models.Cat;
import com.ambrosiaandrade.pets.repositories.AnimalRepository;
import com.ambrosiaandrade.pets.service.AnimalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AnimalControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AnimalService animalService;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private IAnimalMapper animalMapper;

    @InjectMocks
    private AnimalController animalController;

    @BeforeEach
    void setUp() {
        animalService = new AnimalService(animalRepository, animalMapper);
        animalController = new AnimalController(animalService);
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(animalController).build();
    }

    @Test
    @Disabled
    void addAnimal_shouldReturnCreatedResponseWithLocationAndBody() throws Exception {
        Animal savedAnimal = new Cat();
        savedAnimal.setId(42);
        savedAnimal.setName("Findus");

        when(animalService.saveAnimal(any())).thenReturn(savedAnimal);

        String json = """
                {
                    "name": "Findus",
                    "birthday": "2020-01-01",
                    "animalType": "CAT"
                }
                """;
        String expectedLocationPart = "/animal/42";

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/animal")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(expectedLocationPart)));
    }

}