package com.ambrosiaandrade.pets.controller;

import com.ambrosiaandrade.pets.PetsApplication;
import com.ambrosiaandrade.pets.factory.MockAnimal;
import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.service.AdvanceService;
import com.ambrosiaandrade.pets.service.AsyncService;
import com.ambrosiaandrade.pets.service.KafkaService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = PetsApplication.class)
@AutoConfigureMockMvc
class AdvanceAnimalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdvanceService service;

    @MockitoBean
    private KafkaService kafkaService;

    @MockitoBean
    private AsyncService asyncService;

    @Nested
    class Data {

        @Test
        @DisplayName("Populating database")
        void populateDatabase() throws Exception {
            doNothing().when(service).generateAnimalsAndSave(anyInt());

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

    // FIXME
    @Nested
    @Disabled
    class Async {

        @Test
        @DisplayName("Should return 200 OK when async completes successfully")
        void testAsyncSuccess() throws Exception {
            when(asyncService.success())
                    .thenReturn(CompletableFuture.completedFuture("All good"));

            mockMvc.perform(get("/advance/async/success"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("All good"));
        }

        @Test
        @DisplayName("Should handle RuntimeException inside async and return 500")
        void testAsyncRuntimeException() throws Exception {
            when(asyncService.error())
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Simulated failure")));

            mockMvc.perform(get("/advance/async/error"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Simulated failure")));
        }

        @Test
        @DisplayName("Should handle CompletionException wrapping a RuntimeException")
        void testAsyncCompletionException() throws Exception {
            when(asyncService.error())
                    .thenReturn(CompletableFuture.failedFuture(
                            new CompletionException(new IllegalArgumentException("Wrapped inside CompletionException")))
                    );

            mockMvc.perform(get("/advance/async/error"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Wrapped inside CompletionException")));
        }

        @Test
        @DisplayName("Should handle TimeoutException if async takes too long")
        void testAsyncTimeout() throws Exception {
            // Simulate a timeout manually
            CompletableFuture<String> hangingFuture = new CompletableFuture<>();
            when(asyncService.error()).thenReturn(hangingFuture);

            MvcResult result = mockMvc.perform(get("/advance/async/error"))
                    .andDo(print())
                    .andExpect(request().asyncStarted())
                    .andReturn();

            // Manually complete exceptionally with Timeout
            hangingFuture.completeExceptionally(new TimeoutException("Took too long"));

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Took too long")));
        }

        @Test
        @DisplayName("Should handle RejectedExecutionException when async service is saturated")
        void testRejectedExecutionException() throws Exception {
            when(asyncService.error())
                    .thenReturn(CompletableFuture.failedFuture(new RejectedExecutionException("Thread pool is full")));

            mockMvc.perform(get("/advance/async/error"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Thread pool is full")));
        }

        @Test
        @DisplayName("Should handle null pointer inside async task")
        void testAsyncNullPointer() throws Exception {
            when(asyncService.error())
                    .thenReturn(CompletableFuture.failedFuture(new NullPointerException("NPE in async")));

            mockMvc.perform(get("/advance/async/error"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("NPE in async")));
        }

        @Test
        @DisplayName("Should handle illegal argument inside async task")
        void testAsyncIllegalArgument() throws Exception {
            when(asyncService.error())
                    .thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Illegal arg")));

            mockMvc.perform(get("/advance/async/error"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Illegal arg")));
        }

    }

    @Nested
    class Kafka {

        @Test
        void kafkaProduce() throws Exception {
            doNothing().when(kafkaService).sendMessage(anyString());

            mockMvc.perform(
                            get("/advance/kafka/producer")
                                    .param("msg", "Hello kafka")
                    )
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(kafkaService, times(1)).sendMessage(anyString());
        }

        @Test
        void kafkaConsume() throws Exception {
            when(kafkaService.getMessages()).thenReturn(List.of("Hello kafka", "test 1"));

            mockMvc.perform(
                            get("/advance/kafka/consumer")
                    )
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(kafkaService, times(1)).getMessages();
        }

    }

}