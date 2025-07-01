package com.ambrosiaandrade.pets.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Profile("dev")
@Service
public class KafkaProducerService {

    private KafkaTemplate<String, String> kafkaTemplate;
    private RetryTemplate retryTemplate;

    @Value("${app.kafka.topic}")
    private String TOPIC;

    public KafkaProducerService(RetryTemplate retryTemplate, KafkaTemplate<String, String> kafkaTemplate) {
        this.retryTemplate = retryTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String message, boolean retry) {
        if (retry) {
            sendMessageWithRetry(message);
        } else {
            sendMessage(message);
        }
    }

    private void sendMessage(String message) {
        kafkaTemplate.send(TOPIC, message)
                .thenAccept(result -> {
                    var meta = result.getRecordMetadata();
                    log.info("[Kafka_send] Message sent successfully to topic: {}, partition: {}, offset: {}, message: {}",
                            meta.topic(), meta.partition(), meta.offset(), message);
                })
                // If you need to handle retries or await completion elsewhere, consider returning it
                // return result;
                .exceptionally(ex -> {
                    log.error("[Kafka_send] Failed to send message: " + message, ex);
                    return null;
                });
    }

    private void sendMessageWithRetry(String message) {
        RetryCallback<Void, RuntimeException> callback = context -> {
            int attempt = context.getRetryCount() + 1;
            log.info("[Kafka_sendWithRetry] Attempt {} of sending a message: {}", attempt, message);

            try {
                SendResult<String, String> result = kafkaTemplate.send(TOPIC, message).get(); // .get() para propagar exceções
                var meta = result.getRecordMetadata();
                log.info("[Kafka_sendWithRetry] Message sent successfully to topic: {}, partition: {}, offset: {}",
                        meta.topic(), meta.partition(), meta.offset());
            } catch (Exception e) {
                log.warn("[Kafka_sendWithRetry] Error while trying to send message with attempt {}: {}", attempt, e.getMessage());
                throw new RuntimeException(e); // força o retry
            }

            return null;
        };

        RecoveryCallback<Void> recovery = context -> {
            // Callback final após todas as tentativas falharem
            log.error("[Kafka_sendWithRetry] All attempts failed of sending a message: {}", message);
            return null;
        };

        retryTemplate.execute(callback, recovery);
    }

}
