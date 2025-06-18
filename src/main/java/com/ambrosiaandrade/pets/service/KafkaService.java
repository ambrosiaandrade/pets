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
        log.info("[Kafka] Send: " + message);
        kafkaTemplate.send(TOPIC, message);
    }

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void consume(String message) {
        messages.add(message);
        log.info("[Kafka] Consumed: " + message);
    }

    public List<String> getMessages() {
        return messages;
    }

}
