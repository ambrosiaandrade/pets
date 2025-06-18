package com.ambrosiaandrade.pets.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class AsyncService {
    
    @Async
    public CompletableFuture<String> success() {
        try {
            // Simulates a long task
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }

        log.info("[runAsyncTask] Executed in thread: " + Thread.currentThread().getName());
        return CompletableFuture.completedFuture("Finished task!");
    }

    @Async
    public CompletableFuture<String> error() {
        return CompletableFuture.failedFuture(new RuntimeException("Async error!"));
    }

}
