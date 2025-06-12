package com.ambrosiaandrade.pets.controller;

import com.ambrosiaandrade.pets.enums.AnimalTypeEnum;
import com.ambrosiaandrade.pets.factory.MockAnimal;
import com.ambrosiaandrade.pets.interfaces.IAnimalMapper;
import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.models.Cat;
import com.ambrosiaandrade.pets.repositories.AnimalRepository;
import com.ambrosiaandrade.pets.service.AnimalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.ambrosiaandrade.pets.enums.AnimalTypeEnum.CAT;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnimalController.class)
class AnimalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnimalService animalService;

    @Test
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
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(expectedLocationPart)));

        verify(animalService).saveAnimal(any());
    }

    @Test
    void getAnimals_shouldReturnAnimalListAndOkStatus() throws Exception {
        List<Animal> list = MockAnimal.generateAnimals();

        when(animalService.getAnimals()).thenReturn(list);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/animal")
                )
                .andDo(print())
                .andExpect(status().isOk());

        verify(animalService).getAnimals();
    }

    @Test
    void getAnimalByType_shouldReturnAnimalListAndOkStatus() throws Exception {
        List<Animal> list = List.of(MockAnimal.generateAnimal(CAT));

        when(animalService.getAnimalsByType(CAT.name())).thenReturn(list);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/animal/q")
                                .param("type", "CAT")
                )
                .andDo(print())
                .andExpect(status().isOk());

        verify(animalService).getAnimalsByType(CAT.name());
    }

    @Test
    void getAnimalById_shouldReturnAnimalAndOkStatus() throws Exception {
        Animal savedAnimal = MockAnimal.generateAnimal(CAT);
        savedAnimal.setId(1);

        when(animalService.getAnimal(anyInt())).thenReturn(savedAnimal);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/animal/{id}", 1)
                )
                .andDo(print())
                .andExpect(status().isOk());

        verify(animalService).getAnimal(anyInt());
    }

    @Test
    void deleteAnimalById_shouldNoContentStatus() throws Exception {
        doNothing().when(animalService).deleteAnimal(anyInt());

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/animal/{id}", 1)
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(animalService).deleteAnimal(anyInt());
    }

    @Test
    void updateAnimalById_shouldReturnAnimalAndAcceptedStatus() throws Exception {
        Animal savedAnimal = MockAnimal.generateAnimal(CAT);
        savedAnimal.setId(1);

        when(animalService.updateAnimal(any(), anyInt())).thenReturn(savedAnimal);

        String json = """
                {
                    "name": "Findus",
                    "birthday": "2020-01-01",
                    "animalType": "CAT"
                }
                """;

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/animal/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().isAccepted());

        verify(animalService).updateAnimal(any(), anyInt());
    }

}