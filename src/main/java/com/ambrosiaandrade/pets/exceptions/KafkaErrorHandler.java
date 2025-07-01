package com.ambrosiaandrade.pets.exceptions;

import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;

public class KafkaErrorHandler {

    @Bean
    public ConsumerAwareListenerErrorHandler kafkaErrorHandler() {
        return (message, exception, consumer) -> {
            System.err.println("Error processing message: " + message.getPayload());
            return null; // You can log, send to DLQ, or handle accordingly
        };
    }

}
