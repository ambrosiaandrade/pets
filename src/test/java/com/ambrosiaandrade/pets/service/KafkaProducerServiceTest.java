
package com.ambrosiaandrade.pets.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ConsumerFactory<String, String> consumerFactory;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    private static final String TOPIC = "unit-test-topic";

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        Field field = KafkaProducerService.class.getDeclaredField("TOPIC");
        field.setAccessible(true);
        field.set(kafkaProducerService, TOPIC);
    }

    @Test
    void testSendMessage() {
        String message = "Hello Kafka";

        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();

        when(kafkaTemplate.send(anyString(), anyString()))
                .thenReturn(future);

        kafkaProducerService.send(message, false);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(kafkaTemplate, times(1)).send(topicCaptor.capture(), messageCaptor.capture());
        assertEquals(TOPIC, topicCaptor.getValue());
        assertEquals(message, messageCaptor.getValue());
    }

    @Test
    void testSendMessage_success_shouldLogInfo() {
        String message = "Hello Kafka";

        SendResult<String, String> fakeResult = mock(SendResult.class);
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(fakeResult);

        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(future);

        kafkaProducerService.send(message, false);

        verify(kafkaTemplate).send(TOPIC, message);
    }

    @Test
    void testSendMessage_exception() {
        String message = "Hello Kafka";

        // Simulate KafkaTemplate throwing
        when(kafkaTemplate.send(anyString(), anyString()))
                .thenThrow(new RuntimeException("Kafka send failed"));

        // Execute
        assertThrows(RuntimeException.class, () -> kafkaProducerService.send(message, false));

        // Verify it was called
        verify(kafkaTemplate).send(TOPIC, message);
    }

    @Test
    void testSendMessage_whenKafkaSendFails_shouldLogError() {
        String message = "error";

        // Simula falha no envio Kafka
        when(kafkaTemplate.send(anyString(), anyString()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Kafka error")));

        assertDoesNotThrow(() -> kafkaProducerService.send(message, false));

        verify(kafkaTemplate).send(TOPIC, message);
    }

    // Helper method to access private fields via reflection
    private static java.lang.reflect.Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}