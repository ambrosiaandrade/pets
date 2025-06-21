package com.ambrosiaandrade.pets.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class KafkaService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    private final List<String> messages = new CopyOnWriteArrayList<>();

    @Value("${app.kafka.topic}")
    private String TOPIC;

    public void sendMessage(String message) {
        kafkaTemplate.send(TOPIC, message)
                .thenAccept(result -> log.info("[Kafka] Message sent successfully: {}", message))
                .exceptionally(ex -> {
                    log.error("[Kafka] Failed to send message: {}", message, ex);
                    return null;
                });
    }

    @KafkaListener(
            topics = "${app.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        try {
            processMessage(message);
            log.info("[Kafka] Consumed: {}", message);
        } catch (Exception e) {
            log.error("[Kafka] Failed to process message: {}", message, e);
        }
    }

    protected void processMessage(String message) {
        messages.add(message);
    }

    public List<String> getMessages() {
        return messages;
    }

    public List<String> fetchMessagesFromKafka(int maxMessages) {
        List<String> fetchedMessages = new java.util.ArrayList<>();
        KafkaConsumer<String, String> consumer = null;
        try {
            consumer = (KafkaConsumer<String, String>) consumerFactory.createConsumer();
            consumer.subscribe(java.util.Collections.singletonList(TOPIC));
            consumer.poll(java.time.Duration.ZERO); // força atribuição de partições

            int count = 0;
            while (count < maxMessages) {
                ConsumerRecords<String, String> records = consumer.poll(java.time.Duration.ofSeconds(2));
                for (ConsumerRecord<String, String> record : records) {
                    fetchedMessages.add(record.value());
                    count++;
                    if (count >= maxMessages) break;
                }
                consumer.commitSync(); // commit offsets after processing
                if (records.isEmpty()) break;
            }
        } catch (Exception e) {
            log.error("[Kafka] Error fetching messages from Kafka", e);
        } finally {
            if (consumer != null) consumer.close();
        }
        return fetchedMessages;
    }

}
