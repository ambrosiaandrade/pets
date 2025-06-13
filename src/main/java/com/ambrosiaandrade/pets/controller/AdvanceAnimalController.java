package com.ambrosiaandrade.pets.controller;

import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.service.AdvanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/advance")
public class AdvanceAnimalController {

    @Autowired
    private AdvanceService service;

    @GetMapping("/populate/{number}")
    public ResponseEntity<Object> populateDatabase(@PathVariable(required = false) Integer number) {
        if (number == null) number = 1000;
        service.generateAnimalsAndSave(number);
        return ResponseEntity.ok().body("Data generated and populated in database");
    }

    @GetMapping("/no-pagination")
    public ResponseEntity<List<Animal>> getDataNoPagination() {
        var result = service.getDataNoPagination();
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/pagination")
    public ResponseEntity<Page<Animal>> getDataWithPagination(Pageable pageable) {
        var result = service.getDataWithPagination(pageable);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/async")
    public CompletableFuture<ResponseEntity<Object>> getAsync(@RequestParam(defaultValue = "false") boolean isAsyncError) {
        if (isAsyncError) {
            return service.runAsyncTaskError()
                    .handle((handle, ex) -> {
                        if (ex != null) {
                            return ResponseEntity.internalServerError().body(ex.getMessage());
                        }
                        return ResponseEntity.ok().body(handle);
                    });
        } else {
            return service.runAsyncTask()
                    .thenApply(ResponseEntity::ok);
        }
    }
}
