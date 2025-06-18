package com.ambrosiaandrade.pets.controller;

import com.ambrosiaandrade.pets.exceptions.ErrorMessage;
import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.service.AnimalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Animal", description = "Operations related to animals")
@RestController
@RequestMapping("/animal")
public class AnimalController {

    private final AnimalService service;

    public AnimalController(AnimalService service) {
        this.service = service;
    }

    @Operation(
            summary = "Add a new animal",
            description = "Creates a new animal entry in the system and returns the saved animal along with its location URI."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Animal created successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Animal.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}
            )
    })
    @PostMapping
    public ResponseEntity<Object> addAnimal(@RequestBody Animal animal) {
        var savedAnimal = service.saveAnimal(animal);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedAnimal.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedAnimal);
    }

    @Operation(
            summary = "Retrieve an animal by ID",
            description = "Returns an animal in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Animal retrieved successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Animal.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> getAnimalById(@PathVariable int id) {
        return ResponseEntity.ok().body(service.getAnimal(id));
    }

    @Operation(
            summary = "Retrieve all animals by type",
            description = "Returns a list of all animals in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Animals retrieved successfully",
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
    @GetMapping("/q")
    public ResponseEntity<List<Animal>> getAnimalsByType(@RequestParam String type) {
        return ResponseEntity.ok().body(service.getAnimalsByType(type));
    }

    @Operation(
            summary = "Retrieve all animals",
            description = "Returns a list of all animals in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Animals retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Animal.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}
            )
    })
    @GetMapping
    public ResponseEntity<List<Animal>> getAnimals() {
        return ResponseEntity.ok().body(service.getAnimals());
    }

    @Operation(
            summary = "Delete an animal by ID",
            description = "Deletes an animal from the system."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Animal deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAnimalById(@PathVariable int id) {
        service.deleteAnimal(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update an animal by ID",
            description = "Updates an existing animal in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Animal updated successfully", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Animal.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))}
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateAnimalById(@RequestBody Animal animal, @PathVariable int id) {
        var response = service.updateAnimal(animal, id);
        return ResponseEntity.accepted().body(response);
    }

}
