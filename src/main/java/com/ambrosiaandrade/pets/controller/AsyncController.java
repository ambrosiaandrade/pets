package com.ambrosiaandrade.pets.controller;

import com.ambrosiaandrade.pets.service.AsyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Tag(name = "Async Operations", description = "Operations related to asynchronous tasks, including success and error handling.")
@Slf4j
@RestController
@RequestMapping("/async")
public class AsyncController {

    @Autowired
    private AsyncService asyncService;

    @Operation(
            summary = "Simulates an async task"
            , description = "Executes a task asynchronously and returns the result after completion.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Async task completed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/success")
    public CompletableFuture<ResponseEntity<String>> success() {
        return asyncService.success()
                .thenApply(ResponseEntity::ok);
    }

    @Operation(
            summary = "Simulates an async task that fails",
            description = "Executes a task asynchronously that throws an error and returns the error message."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Async task completed with error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/error")
    public CompletableFuture<ResponseEntity<Object>> error() {
        return asyncService.error()
                .handle((result, ex) -> {
                    if (ex != null) {
                        Throwable cause = (ex instanceof CompletionException) ? ex.getCause() : ex;
                        return ResponseEntity.internalServerError().body(cause.getMessage());
                    }
                    return ResponseEntity.ok(result);
                });
    }

}
