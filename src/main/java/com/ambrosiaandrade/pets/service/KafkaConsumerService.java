package com.ambrosiaandrade.pets.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Profile("dev")
@Service
public class KafkaConsumerService {

    private final ConsumerFactory<String, String> consumerFactory;

    public KafkaConsumerService(ConsumerFactory<String, String> consumerFactory) {
        this.consumerFactory = consumerFactory;
    }

    @Value("${app.kafka.topic}")
    private String TOPIC;

    public List<String> fetchMessagesFromKafka(int maxMessages) {
        List<String> messages = new ArrayList<>();

        try (KafkaConsumer<String, String> consumer = (KafkaConsumer<String, String>) consumerFactory.createConsumer()) {
            consumer.subscribe(List.of(TOPIC));
            consumer.poll(Duration.ZERO); // força a atribuição de partições

            int count = 0;

            while (count < maxMessages) {
                var records = consumer.poll(Duration.ofSeconds(2));

                for (var record : records) {
                    messages.add(record.value());
                    if (++count >= maxMessages) break;
                }

                consumer.commitSync();

                if (records.isEmpty()) break; // se não veio nada, encerra
            }

        } catch (Exception e) {
            log.error("[Kafka] Erro ao buscar mensagens", e);
        }

        return messages;
    }

}
