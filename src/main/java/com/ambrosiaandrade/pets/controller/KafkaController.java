package com.ambrosiaandrade.pets.controller;

import com.ambrosiaandrade.pets.service.KafkaService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Kafka Operations", description = "Operations related to Kafka messaging, including producing and consuming messages.")
@Slf4j
@RestController
@RequestMapping("/kafka")
public class KafkaController {

    @Autowired
    private KafkaService kafkaService;

    @Operation(
            summary = "Produce a message to Kafka",
            description = "Sends a message to a Kafka topic."
    )
    @Parameter(
            name = "msg",
            description = "The message to send to Kafka. Default is 'Hello kafka'.",
            required = false,
            example = "Hello kafka"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Message sent successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/producer")
    public ResponseEntity<Object> kafkaProduce(@RequestParam(defaultValue = "Hello kafka") String msg) {
        kafkaService.sendMessage(msg);
        return ResponseEntity.ok("Message sent: " + msg);
    }

    @Operation(
            summary = "Consume messages from Kafka",
            description = "Retrieves all the messages from a Kafka topic."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Messages consumed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = String.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            )
    })
    @GetMapping("/consumer")
    public ResponseEntity<Object> kafkaConsume() {
        return ResponseEntity.ok("Message consumed: " + kafkaService.getMessages());
    }

}
