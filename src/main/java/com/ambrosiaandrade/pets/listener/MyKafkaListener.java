package com.ambrosiaandrade.pets.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class MyKafkaListener {

    private final List<String> messages = new CopyOnWriteArrayList<>();

    @KafkaListener(
            topics = "${app.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        try {
            if (message.contains("error")) {
                throw new RuntimeException("Failed processing message");
            }
            processMessage(message);
            log.info("[Kafka_consume] Consumed: {}", message);
        } catch (Exception e) {
            log.error("[Kafka_consume] Failed to process message: {}", message, e);
        }
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 2000),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    @KafkaListener(
            topics = "${app.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consumeWithRetry(String message) {
        try {
            if (message.contains("retry")) {
                throw new RuntimeException("Temporary failure");
            }
            processMessage(message);
            log.info("[Kafka_consumeWithRetry] Consumed: {}", message);
        } catch (Exception e) {
            log.error("[Kafka_consumeWithRetry] Failed to process message: {}", message, e);
        }
    }

    private void processMessage(String message) {
        messages.add(message);
    }

    public List<String> getMessages() {
        return messages;
    }

}
