package com.ambrosiaandrade.pets.controller;

import com.ambrosiaandrade.pets.factory.MockAnimal;
import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.service.AdvanceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaginationController.class)
class PaginationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdvanceService service;

    @Nested
    class Data {

        @Test
        @DisplayName("Populating database")
        void populateDatabase() throws Exception {
            when(service.generateAnimalsAndSave(anyInt())).thenReturn(List.of("For"));

            mockMvc.perform(
                            get("/advance/populate")
                                    .param("number", "40")
                    )
                    .andDo(print())
                    .andExpect(status().isOk());

            mockMvc.perform(
                            get("/advance/populate")
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Populating database but error")
        void populateDatabase_error() throws Exception {
            when(service.generateAnimalsAndSave(anyInt())).thenReturn(List.of());

            mockMvc.perform(
                            get("/advance/populate")
                    )
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Get data with cache")
        void getDataNoPaginationWithCache() throws Exception {
            List<Animal> list = MockAnimal.generateAnimals();
            when(service.getDataNoPaginationButWithCache()).thenReturn(list);

            mockMvc.perform(
                            get("/advance/no-pagination-with-cache")
                    )
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(service, times(1)).getDataNoPaginationButWithCache();
        }

        @Test
        @DisplayName("Get data without cache and no pagination")
        void getDataNoPagination() throws Exception {
            List<Animal> list = MockAnimal.generateAnimals();
            when(service.getDataNoPagination()).thenReturn(list);

            mockMvc.perform(
                            get("/advance/no-pagination")
                    )
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(service, times(1)).getDataNoPagination();
        }

        @Test
        @DisplayName("Get data without cache but pagination")
        void getDataWithPagination() throws Exception {
            List<Animal> list = MockAnimal.generateAnimals();
            Page<Animal> page = new PageImpl<>(list);
            when(service.getDataWithPagination(any())).thenReturn(page);

            mockMvc.perform(
                            get("/advance/pagination")
                    )
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(service, times(1)).getDataWithPagination(any());
        }
    }

}