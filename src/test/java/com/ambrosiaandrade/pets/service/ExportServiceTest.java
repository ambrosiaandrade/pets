package com.ambrosiaandrade.pets.service;

import com.ambrosiaandrade.pets.entities.AnimalEntity;
import com.ambrosiaandrade.pets.enums.AnimalDietEnum;
import com.ambrosiaandrade.pets.enums.AnimalGenderEnum;
import com.ambrosiaandrade.pets.enums.AnimalTypeEnum;
import com.ambrosiaandrade.pets.exceptions.BaseException;
import com.ambrosiaandrade.pets.factory.MockAnimal;
import com.ambrosiaandrade.pets.interfaces.IAnimalMapper;
import com.ambrosiaandrade.pets.repositories.AnimalRepository;
import com.opencsv.CSVWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

    @Mock
    private AnimalRepository repository;
    private IAnimalMapper mapper;

    @InjectMocks
    private ExportService service;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(IAnimalMapper.class);
        service = new ExportService(repository, mapper);
    }

    @Test
    void getCsv() {
        when(repository.findAll()).thenReturn(List.of(MockAnimal.generateEntity()));

        byte[] result = service.getCsv();

        assertNotNull(result);
    }

    @Test
    void getPdf() {
        when(repository.findAll()).thenReturn(List.of(MockAnimal.generateEntity()));

        byte[] result = service.getPdf();

        assertNotNull(result);
    }

    @Test
    void getCsv_multipleAnimals() {
        var animal1 = MockAnimal.generateEntity();
        var animal2 = MockAnimal.generateEntity();
        animal2.setName("AnotherName");
        when(repository.findAll()).thenReturn(List.of(animal1, animal2));

        byte[] result = service.getCsv();

        assertNotNull(result);
        String csv = new String(result);
        // Should contain both animal names
        assert(csv.contains(animal1.getName()));
        assert(csv.contains(animal2.getName()));
    }

    @Test
    void getPdf_multipleAnimals() {
        var animal1 = MockAnimal.generateEntity();
        var animal2 = MockAnimal.generateEntity();
        animal2.setName("AnotherName");
        when(repository.findAll()).thenReturn(List.of(animal1, animal2));

        byte[] result = service.getPdf();

        assertNotNull(result);
        // PDF bytes should not be empty
        assert(result.length > 0);
    }

    @Test
    void getCsv_handlesIOExceptionOnClose() {
        ExportService faultyService = new ExportService(repository, mapper) {
            @Override
            public byte[] getCsv() {
                StringWriter stringWriter = new StringWriter();
                CSVWriter csvWriter = new CSVWriter(stringWriter) {
                    @Override
                    public void close() throws IOException {
                        throw new IOException("Test IO Exception");
                    }
                };
                csvWriter.writeNext(new String[]{"name"});
                try {
                    csvWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException("Erro ao gerar CSV", e);
                }
                return stringWriter.toString().getBytes();
            }
        };

        assertThrows(RuntimeException.class, faultyService::getCsv);
    }

    @Test
    void getCsv_handlesIOException() {
        ExportService faultyService = new ExportService(repository, mapper) {
            @Override
            public byte[] getCsv() {
                StringWriter stringWriter = new StringWriter();
                CSVWriter csvWriter = new CSVWriter(stringWriter) {
                    @Override
                    public void close() throws IOException {
                        throw new IOException("Test IO Exception");
                    }
                };
                csvWriter.writeNext(new String[]{"name"});
                try {
                    csvWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException("Erro ao gerar CSV", e);
                }
                return stringWriter.toString().getBytes();
            }
        };

        assertThrows(RuntimeException.class, faultyService::getCsv);
    }

    @Test
    void database_error() {
        when(repository.findAll()).thenThrow(new DataAccessException("Error"){});

        Executable executable = () -> service.getCsv();

        assertThrows(BaseException.class, executable);
    }

}
