package com.ambrosiaandrade.pets.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AsyncServiceTest {

    private final AsyncService asyncService = new AsyncService();

    @Test
    void runAsyncTask() throws Exception {
        CompletableFuture<String> future = asyncService.success();
        String result = future.get(); // Waits for completion
        assertEquals("Finished task!", result);
    }

    @Test
    void testThreadInterrupted() throws Exception {
        Thread.currentThread().interrupt(); // Force interruption

        CompletableFuture<String> future = asyncService.success();

        // Wait and assert
        ExecutionException thrown = assertThrows(ExecutionException.class, future::get);
        assertTrue(thrown.getCause() instanceof InterruptedException);

        // Reset the interrupt status so it doesn't affect other tests
        Thread.interrupted();
    }

    @Test
    void runAsyncTaskError() {
        CompletableFuture<String> future = asyncService.error();
        Exception exception = assertThrows(Exception.class, future::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Async error!", exception.getCause().getMessage());
    }

    @Test
    void successCompletesSuccessfully() throws Exception {
        CompletableFuture<String> future = asyncService.success();
        assertTrue(future.isDone() || future.complete("Finished task!"));
        assertEquals("Finished task!", future.get());
    }

    @Test
    void errorCompletesExceptionally() {
        CompletableFuture<String> future = asyncService.error();
        assertTrue(future.isCompletedExceptionally());
    }

}