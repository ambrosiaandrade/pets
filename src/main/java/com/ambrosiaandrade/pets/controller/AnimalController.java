package com.ambrosiaandrade.pets.controller;

import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.service.AnimalService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/animal")
public class AnimalController {

    private final AnimalService service;

    public AnimalController(AnimalService service) {
        this.service = service;
    }

    /**
     * Endpoint to add a new animal.
     * It saves the animal and returns a response with the created status and the saved animal.
     * The location of the newly created resource is set in the response header.
     *
     * @param animal The animal to be added.
     * @return ResponseEntity containing the saved animal and the location URI.
     */
    @PostMapping
    public ResponseEntity<Animal> addAnimal(@RequestBody Animal animal) {
        var savedAnimal = service.saveAnimal(animal);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedAnimal.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedAnimal);
    }

    /**
     * Endpoint to retrieve an animal by its ID.
     * It returns a response with the animal if found, or a 404 status if not found.
     *
     * @param id The ID of the animal to be retrieved.
     * @return ResponseEntity containing the animal or a 404 status.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Animal> getAnimalById(@PathVariable int id) {
        return new ResponseEntity<>(service.getAnimal(id), HttpStatusCode.valueOf(200));
    }

    @GetMapping("/q")
    public ResponseEntity<List<Animal>> getAnimalsByType(@RequestParam String type) {
        return new ResponseEntity<>(service.getAnimalsByType(type), HttpStatusCode.valueOf(200));
    }

    /**
     * Endpoint to retrieve all animals.
     * It returns a response with a list of all animals.
     *
     * @return ResponseEntity containing a list of animals.
     */
    @GetMapping
    public ResponseEntity<List<Animal>> getAnimals() {
        return new ResponseEntity<>(service.getAnimals(), HttpStatusCode.valueOf(200));
    }

    /**
     * Endpoint to delete an animal by its ID.
     * It deletes the animal and returns a 204 status if successful.
     *
     * @param id The ID of the animal to be deleted.
     */
    @DeleteMapping("/{id}")
    public void deleteAnimalById(@PathVariable int id) {
        service.deleteAnimal(id);
    }

    /**
     * Endpoint to update an animal by its ID.
     * It updates the animal and returns a 202 status if successful.
     *
     * @param animal The animal data to be updated.
     * @param id The ID of the animal to be updated.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateAnimalById(@RequestBody Animal animal, @PathVariable int id) {
        var response = service.updateAnimal(animal, id);
        return ResponseEntity.accepted().body(response);
    }

}
