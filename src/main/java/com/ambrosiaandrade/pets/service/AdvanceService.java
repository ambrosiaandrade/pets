package com.ambrosiaandrade.pets.service;

import com.ambrosiaandrade.pets.entities.AnimalEntity;
import com.ambrosiaandrade.pets.interfaces.IAnimalMapper;
import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.repositories.AnimalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
public class AdvanceService {

    private final AnimalRepository repository;
    private final IAnimalMapper mapper;
    private final AdvanceUtil util;

    @Value("${app.animal.limit:5000}")
    private Long animalLimit;

    public AdvanceService(AnimalRepository repository, IAnimalMapper mapper, AdvanceUtil util) {
        this.repository = repository;
        this.mapper = mapper;
        this.util = util;
    }

    public List<String> generateAnimalsAndSave(int number) {
        if (number <= 0) return List.of("Invalid number");
        if (!verifyDataLimit()) return List.of("Maximum data limit");

        List<String> list = new ArrayList<>();

        try {
            list.add(runAndLog("For(;;)", number, util::generateAnimalsWithFor));
            list.add(runAndLog("IntStream", number, util::generateAnimalWithIntStream));
            list.add(runAndLog("IntStream_parallel", number, util::generateAnimalWithIntStreamAndParallel));
        } catch (DataAccessException e) {
            log.error(e.getMessage());
        }
        return list;
    }

    private String runAndLog(String label, Integer number, Function<Integer, List<AnimalEntity>> generator) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<AnimalEntity> entities = generator.apply(number);
        repository.saveAll(entities);

        stopWatch.stop();

        String result = String.format("[%s] Saved %s new entities in %s ms", label, entities.size(), stopWatch.getTotalTimeMillis());
        log.info(result);
        return result;
    }

    private boolean verifyDataLimit() {
        long count = repository.count();
        if (count >= animalLimit) {
            log.warn("Animal limit reached: {}", count);
            return false;
        }
        return true;
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

    @Cacheable("AllData")
    public List<Animal> getDataNoPaginationButWithCache() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        var list = repository.findAll()
                .stream()
                .map(mapper::toModel)
                .toList();
        stopWatch.stop();
        log.info(String.format("[getDataNoPaginationWithCache] With time %s ms", stopWatch.getTotalTimeMillis()));
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

}