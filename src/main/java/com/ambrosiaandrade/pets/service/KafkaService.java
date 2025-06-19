package com.ambrosiaandrade.pets.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class KafkaService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final List<String> messages = new CopyOnWriteArrayList<>();

    private static final String TOPIC = "test-topic";

    public void sendMessage(String message) {
        kafkaTemplate.send(TOPIC, message)
                .thenAccept(result -> log.info("[Kafka] Message sent successfully: {}", message))
                .exceptionally(ex -> {
                    log.error("[Kafka] Failed to send message: {}", message, ex);
                    return null;
                });
    }

    @KafkaListener(topics = TOPIC, groupId = "test-group")
    public void consume(String message) {
        try {
            addMessage(message);
            log.info("[Kafka] Consumed: {}", message);
        } catch (Exception e) {
            log.error("[Kafka] Failed to process message: {}", message, e);
        }
    }

    protected void addMessage(String message) {
        messages.add(message);
    }

    public List<String> getMessages() {
        return messages;
    }

}
