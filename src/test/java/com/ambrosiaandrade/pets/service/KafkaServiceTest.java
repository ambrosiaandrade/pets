
package com.ambrosiaandrade.pets.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


class KafkaServiceTest {

    private KafkaTemplate<String, String> kafkaTemplate;
    private KafkaService kafkaService;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        kafkaService = new KafkaService();
        // Inject mock KafkaTemplate
        var kafkaTemplateField = getField(KafkaService.class, "kafkaTemplate");
        kafkaTemplateField.setAccessible(true);
        try {
            kafkaTemplateField.set(kafkaService, kafkaTemplate);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSendMessage() {
        String message = "Hello Kafka";
        kafkaService.sendMessage(message);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(kafkaTemplate, times(1)).send(topicCaptor.capture(), messageCaptor.capture());
        assertEquals("test-topic", topicCaptor.getValue());
        assertEquals(message, messageCaptor.getValue());
    }

    @Test
    void testConsumeAddsMessage() {
        String message = "Consumed message";
        kafkaService.consume(message);

        List<String> messages = kafkaService.getMessages();
        assertEquals(1, messages.size());
        assertEquals(message, messages.get(0));
    }

    @Test
    void testGetMessagesReturnsAllConsumedMessages() {
        kafkaService.consume("msg1");
        kafkaService.consume("msg2");

        List<String> messages = kafkaService.getMessages();
        assertEquals(2, messages.size());
        assertTrue(messages.contains("msg1"));
        assertTrue(messages.contains("msg2"));
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