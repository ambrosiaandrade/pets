package com.ambrosiaandrade.pets.controller;

import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.service.AnimalService;
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

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAnimalById(@PathVariable int id) {
        return ResponseEntity.ok().body(service.getAnimal(id));
    }

    @GetMapping("/q")
    public ResponseEntity<List<Animal>> getAnimalsByType(@RequestParam String type) {
        return ResponseEntity.ok().body(service.getAnimalsByType(type));
    }

    @GetMapping
    public ResponseEntity<List<Animal>> getAnimals() {
        return ResponseEntity.ok().body(service.getAnimals());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAnimalById(@PathVariable int id) {
        service.deleteAnimal(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateAnimalById(@RequestBody Animal animal, @PathVariable int id) {
        var response = service.updateAnimal(animal, id);
        return ResponseEntity.accepted().body(response);
    }

}
