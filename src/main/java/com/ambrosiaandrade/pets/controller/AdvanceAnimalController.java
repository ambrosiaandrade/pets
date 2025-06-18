package com.ambrosiaandrade.pets.controller;

import com.ambrosiaandrade.pets.exceptions.ErrorMessage;
import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.service.AdvanceService;
import com.ambrosiaandrade.pets.service.AsyncService;
import com.ambrosiaandrade.pets.service.KafkaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

// FIXME
@Tag(name = "Advance Animal", description = "Advanced operations related to animals, including database population, pagination, async tasks, and Kafka messaging.")
@Slf4j
@RestController
@RequestMapping("/advance")
public class AdvanceAnimalController {

    @Autowired
    private AdvanceService service;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private AsyncService asyncService;

    @Operation(
            summary = "Populate the database with random animal data",
            description = "Generates a specified number of random animal entries and saves them to the database."
    )
    @Parameter(
            name = "number",
            description = "Number of animals to be generated.",
            required = false,
            example = "100"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Data generated and populated in database",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/populate")
    public ResponseEntity<Object> populateDatabase(@RequestParam(required = false, defaultValue = "100") Integer number) {
        service.generateAnimalsAndSave(number);
        return ResponseEntity.ok().body("Data generated and populated in database");
    }

    @Operation(
            summary = "Get the number of animals in the database",
            description = "Returns all animals entries in the database."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Data retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Animal.class))
                    )),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))})
    })
    @GetMapping("/no-pagination-with-cache")
    public ResponseEntity<List<Animal>> getDataNoPaginationWithCache() {
        var result = service.getDataNoPaginationButWithCache();
        return ResponseEntity.ok().body(result);
    }

    @Operation(
            summary = "Get the number of animals in the database",
            description = "Returns all animals entries in the database without pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Data retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Animal.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}
            )
    })
    @GetMapping("/no-pagination")
    public ResponseEntity<List<Animal>> getDataNoPagination() {
        var result = service.getDataNoPagination();
        return ResponseEntity.ok().body(result);
    }

    @Operation(
            summary = "Get the number of animals in the database",
            description = "Returns all animals entries in the database with pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Data retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}
            )
    })
    @GetMapping("/pagination")
    public ResponseEntity<Page<Animal>> getDataWithPagination(Pageable pageable) {
        var result = service.getDataWithPagination(pageable);
        return ResponseEntity.ok().body(result);
    }

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
    @GetMapping("/async/success")
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
    @GetMapping("/async/error")
    public CompletableFuture<ResponseEntity<Object>> error() {
        return asyncService.error()
                .handle((result, ex) -> {
                    if (ex != null) {
                        Throwable cause = (ex instanceof CompletionException) ? ex.getCause() : ex;
                        return ResponseEntity.internalServerError().body("Error: " + cause.getMessage());
                    }
                    return ResponseEntity.ok(result);
                });
    }

    @Operation(
            summary = "Produce a message to Kafka",
            description = "Sends a message to a Kafka topic."
    )
    @Parameter(
            name = "msg",
            description = "The message to send to Kafka. Default is 'Hello kafka'.",
            required = false,
            example = "Hello kafka"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Message sent successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/kafka/producer")
    public ResponseEntity<Object> kafkaProduce(@RequestParam(defaultValue = "Hello kafka") String msg) {
        kafkaService.sendMessage(msg);
        return ResponseEntity.ok("Message sent: " + msg);
    }

    @Operation(
            summary = "Consume messages from Kafka",
            description = "Retrieves all the messages from a Kafka topic."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Messages consumed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/kafka/consumer")
    public ResponseEntity<Object> kafkaConsume() {
        return ResponseEntity.ok("Message consumed: " + kafkaService.getMessages());
    }

}
