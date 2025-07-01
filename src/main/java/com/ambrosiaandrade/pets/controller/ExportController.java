package com.ambrosiaandrade.pets.controller;

import com.ambrosiaandrade.pets.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "4. Export controller", description = "Controller for exporting data in different formats")
@RestController
@RequestMapping("/export")
public class ExportController {

    private final ExportService service;

    public ExportController(ExportService service) {
        this.service = service;
    }

    @Operation(summary = "Export data in CSV format",
            description = "Exports the data in CSV format. The response is a CSV file containing the exported data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "CSV file exported successfully",
                    content = {@Content(mediaType = "text/csv", schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error while exporting CSV",
                    content = {@Content(mediaType = "text/csv")})
    })
    @GetMapping("/csv")
    public ResponseEntity<byte[]> getCsv() {
        var bytes = service.getCsv();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pets.csv");
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @Operation(summary = "Export data in PDF format",
            description = "Exports the data in PDF format. The response is a PDF file containing the exported data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "PDF file exported successfully",
                    content = {@Content(mediaType = "application/pdf", schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error while exporting PDF",
                    content = {@Content(mediaType = "application/pdf")})
    })
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> getPdf() {
        var bytes = service.getPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pets.pdf");
        headers.set(HttpHeaders.CONTENT_TYPE, "application/pdf");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

}
