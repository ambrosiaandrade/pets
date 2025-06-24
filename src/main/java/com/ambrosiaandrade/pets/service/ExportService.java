package com.ambrosiaandrade.pets.service;

import com.ambrosiaandrade.pets.exceptions.BaseException;
import com.ambrosiaandrade.pets.interfaces.IAnimalMapper;
import com.ambrosiaandrade.pets.models.Animal;
import com.ambrosiaandrade.pets.repositories.AnimalRepository;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
public class ExportService {

    private final AnimalRepository repository;
    private final IAnimalMapper mapper;

    public ExportService(AnimalRepository repository, IAnimalMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public byte[] getCsv() {
        var list = fetchAnimals();

        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);

        String[] header = {"name", "age", "age_in_human_years", "birthday", "type", "diet", "gender"};
        csvWriter.writeNext(header);

        for (Animal animal : list) {
            String[] line = {
                    animal.getName(),
                    String.valueOf(animal.getAge()),
                    String.valueOf(animal.getAgeInHumanYears()),
                    String.valueOf(animal.getBirthday()),
                    animal.getType().name(),
                    animal.getDiet().name(),
                    animal.getGender().name()
            };
            csvWriter.writeNext(line);
        }

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar CSV", e);
        }

        return stringWriter.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getPdf() {
        var list = fetchAnimals();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document();
            PdfWriter.getInstance(doc, baos);
            doc.open();

            doc.add(new Paragraph("Animals report"));
            doc.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);

            table.addCell("name");
            table.addCell("age");
            table.addCell("age_in_human_years");
            table.addCell("birthday");
            table.addCell("type");
            table.addCell("diet");
            table.addCell("gender");

            for (Animal animal : list) {
                table.addCell(animal.getName());
                table.addCell(String.valueOf(animal.getAge()));
                table.addCell(String.valueOf(animal.getAgeInHumanYears()));
                table.addCell(String.valueOf(animal.getBirthday()));
                table.addCell(animal.getType().name());
                table.addCell(animal.getDiet().name());
                table.addCell(animal.getGender().name());
            }

            doc.add(table);
            doc.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }

    private List<Animal> fetchAnimals() {
        try {
            var entities = repository.findAll();
            return entities.stream()
                    .map(mapper::toModel)
                    .toList();
        } catch (DataAccessException e) {
            StackTraceElement stackTraceElement = e.getStackTrace()[0];
            log.error(stackTraceElement.toString());
            throw new BaseException(e.getMessage(), 500);
        }
    }



}
