package com.ambrosiaandrade.pets.controller;

import com.ambrosiaandrade.pets.service.AsyncService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AsyncController.class)
class AsyncControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AsyncService asyncService;

    @Nested
    class Async {

        @Test
        @DisplayName("Should return 200 OK when async completes successfully")
        void testAsyncSuccess() throws Exception {
            String expected = "All good";
            when(asyncService.success())
                    .thenReturn(CompletableFuture.completedFuture(expected));

            MvcResult result = mockMvc.perform(get("/async/success"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            ResponseEntity<Object> response = (ResponseEntity<Object>) result.getAsyncResult();
            assertTrue(expected.equalsIgnoreCase(Objects.requireNonNull(response.getBody()).toString()));
        }

        @Test
        @DisplayName("Should handle RuntimeException inside async and return 500")
        void testAsyncRuntimeException() throws Exception {
            String expected = "Simulated failure";
            when(asyncService.error())
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException(expected)));

            MvcResult result = mockMvc.perform(get("/async/error"))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mockMvc.perform(asyncDispatch(result))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andReturn();

            ResponseEntity<Object> response = (ResponseEntity<Object>) result.getAsyncResult();
            assertTrue(expected.equalsIgnoreCase(Objects.requireNonNull(response.getBody()).toString()));
        }

        @Test
        @DisplayName("Should handle CompletionException wrapping a RuntimeException")
        void testAsyncCompletionException() throws Exception {
            when(asyncService.error())
                    .thenReturn(CompletableFuture.failedFuture(
                            new CompletionException(new IllegalArgumentException("Wrapped inside CompletionException")))
                    );

            MvcResult result = mockMvc.perform(get("/async/error"))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Wrapped inside CompletionException")));
        }

        @Test
        @DisplayName("Should handle TimeoutException if async takes too long")
        void testAsyncTimeout() throws Exception {
            // Simulate a timeout manually
            CompletableFuture<String> hangingFuture = new CompletableFuture<>();
            when(asyncService.error()).thenReturn(hangingFuture);

            MvcResult result = mockMvc.perform(get("/async/error"))
                    .andDo(print())
                    .andExpect(request().asyncStarted())
                    .andReturn();

            // Manually complete exceptionally with Timeout
            hangingFuture.completeExceptionally(new TimeoutException("Took too long"));

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Took too long")));
        }

        @Test
        @DisplayName("Should handle RejectedExecutionException when async service is saturated")
        void testRejectedExecutionException() throws Exception {
            when(asyncService.error())
                    .thenReturn(CompletableFuture.failedFuture(new RejectedExecutionException("Thread pool is full")));

            MvcResult restult = mockMvc.perform(get("/async/error"))
                    .andDo(print())
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mockMvc.perform(asyncDispatch(restult))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Thread pool is full")));
        }

        @Test
        @DisplayName("Should handle null pointer inside async task")
        void testAsyncNullPointer() throws Exception {
            when(asyncService.error())
                    .thenReturn(CompletableFuture.failedFuture(new NullPointerException("NPE in async")));

            MvcResult result = mockMvc.perform(get("/async/error"))
                    .andDo(print())
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("NPE in async")));
        }

        @Test
        @DisplayName("Should handle illegal argument inside async task")
        void testAsyncIllegalArgument() throws Exception {
            when(asyncService.error())
                    .thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Illegal arg")));

            MvcResult result = mockMvc.perform(get("/async/error"))
                    .andDo(print())
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Illegal arg")));
        }

        @Test
        @DisplayName("Should return 200 OK when no exception occurs in async method")
        void testAsyncSuccessWhenNoException() throws Exception {
            when(asyncService.error())
                    .thenReturn(CompletableFuture.completedFuture("All good"));

            MvcResult result = mockMvc.perform(get("/async/error"))
                    .andExpect(request().asyncStarted())
                    .andReturn();

            mockMvc.perform(asyncDispatch(result))
                    .andExpect(status().isOk())
                    .andExpect(content().string("All good"));
        }

    }

}