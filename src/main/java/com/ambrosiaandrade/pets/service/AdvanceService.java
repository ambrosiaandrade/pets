package com.ambrosiaandrade.pets.service;

import com.ambrosiaandrade.pets.entities.AnimalEntity;
import com.ambrosiaandrade.pets.interfaces.IAnimalMapper;
import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.repositories.AnimalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Slf4j
@Service
public class AdvanceService {

    @Autowired
    private AnimalRepository repository;

    @Autowired
    private IAnimalMapper mapper;

    @Autowired
    private AdvanceUtil util;

    public void generateAnimalsAndSave(int number) {
        try {
            runAndLog("For(;;)", number, util::generateAnimalsWithFor);
            runAndLog("IntStream", number, util::generateAnimalWithIntStream);
            runAndLog("IntStream_parallel", number, util::generateAnimalWithIntStreamAndParallel);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
        }
    }

    private void runAndLog(String label, Integer number, Function<Integer, List<AnimalEntity>> generator) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<AnimalEntity> entities = generator.apply(number);
        repository.saveAll(entities);

        stopWatch.stop();
        log.info("[{}] Saved {} new entities in {} ms", label, entities.size(), stopWatch.getTotalTimeMillis());
    }

    public List<Animal> getDataNoPagination() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        var list = repository.findAll()
                .stream()
                .map(mapper::toModel)
                .toList();
        stopWatch.stop();
        log.info(String.format("[getDataNoPagination] With time %s ms", stopWatch.getTotalTimeMillis()));
        return list;
    }

    public Page<Animal> getDataWithPagination(Pageable pageable) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        var list = repository.findAll(pageable)
                .map(mapper::toModel);
        stopWatch.stop();
        log.info(String.format("[getDataWithPagination] With time %s ms", stopWatch.getTotalTimeMillis()));
        return list;
    }

    @Async
    public CompletableFuture<String> runAsyncTask() {
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
    public CompletableFuture<String> runAsyncTaskError() {
        try {
            throw new RuntimeException("Simulated error");
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

}
