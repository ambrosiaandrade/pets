package com.ambrosiaandrade.pets.controller;

import com.ambrosiaandrade.pets.listener.MyKafkaListener;
import com.ambrosiaandrade.pets.service.KafkaConsumerService;
import com.ambrosiaandrade.pets.service.KafkaProducerService;
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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "4. Kafka Operations", description = "Operations related to Kafka messaging, including producing and consuming messages.")
@Slf4j
@RestController
@RequestMapping("/kafka")
public class KafkaController {

    private KafkaProducerService kafkaProducerService;
    private KafkaConsumerService kafkaConsumerService;
    private MyKafkaListener listener;

    private static final String STATUS = "status";
    private static final String MESSAGE = "message";

    public KafkaController(KafkaProducerService kafkaProducerService, KafkaConsumerService kafkaConsumerService, MyKafkaListener listener) {
        this.kafkaProducerService = kafkaProducerService;
        this.kafkaConsumerService = kafkaConsumerService;
        this.listener = listener;
    }

    @Operation(
            summary = "Produce a message to Kafka",
            description = "Sends a message to a Kafka topic."
    )
    @Parameter(
            name = "message",
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
    public ResponseEntity<Map<String, String>> kafkaProduce(@RequestParam(defaultValue = "Hello kafka") String message, @RequestParam(required = false, defaultValue = "false") boolean retry) {
        kafkaProducerService.send(message, retry);

        Map<String, String> response = new HashMap<>();
        response.put(STATUS, "Message sent");
        response.put(MESSAGE, message);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Retrieves all messages from Kafka",
            description = "Retrieves all the messages from a Kafka topic, from the beginning."
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
    public ResponseEntity<Map<String, String>> kafkaConsume() {

        Map<String, String> response = new HashMap<>();
        response.put(STATUS, "Message consumed");
        response.put(MESSAGE, listener.getMessages().toString());

        return ResponseEntity.ok(response);
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
    @GetMapping("/consumer/{number}")
    public ResponseEntity<Map<String, String>> kafkaConsume(@PathVariable int number) {

        Map<String, String> response = new HashMap<>();
        response.put(STATUS, "Message consumed");
        response.put(MESSAGE, kafkaConsumerService.fetchMessagesFromKafka(number).toString());

        return ResponseEntity.ok(response);
    }

}
