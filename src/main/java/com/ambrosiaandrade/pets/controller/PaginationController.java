package com.ambrosiaandrade.pets.controller;

import com.ambrosiaandrade.pets.exceptions.ErrorMessage;
import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.service.AdvanceService;
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

@Tag(name = "2. Advanced Animal", description = "Advanced operations related to animals, including database population, cache and pagination")
@Slf4j
@RestController
@RequestMapping("/advance")
public class PaginationController {

    @Autowired
    private AdvanceService service;

    @Operation(
            summary = "Populate the database with random animal data",
            description = "Generates a specified number of random animal entries and saves them to the database."
    )
    @Parameter(
            name = "number",
            description = "Number of animals to be generated will be the triple.",
            required = false,
            example = "100"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Data generated and populated in database",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = List.class)))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/populate")
    public ResponseEntity<Object> populateDatabase(@RequestParam(required = false, defaultValue = "100") Integer number) {
        var result = service.generateAnimalsAndSave(number);
        if (!result.isEmpty())
            return ResponseEntity.ok().body(result);
        return ResponseEntity.internalServerError().body("Error while generating data");
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

}
