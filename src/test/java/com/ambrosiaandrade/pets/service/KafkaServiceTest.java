
package com.ambrosiaandrade.pets.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ConsumerFactory<String, String> consumerFactory;

    @InjectMocks
    private KafkaService kafkaService;
    
    private static final String TOPIC = "unit-test-topic";

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        Field field = KafkaService.class.getDeclaredField("TOPIC");
        field.setAccessible(true);
        field.set(kafkaService, TOPIC);
    }

    @Test
    void testSendMessage() {
        String message = "Hello Kafka";

        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();

        when(kafkaTemplate.send(anyString(), anyString()))
                .thenReturn(future);

        kafkaService.sendMessage(message);

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

        kafkaService.sendMessage(message);

        verify(kafkaTemplate).send(TOPIC, message);
    }

    @Test
    void testSendMessage_exception() {
        String message = "Hello Kafka";

        // Simulate KafkaTemplate throwing
        when(kafkaTemplate.send(anyString(), anyString()))
                .thenThrow(new RuntimeException("Kafka send failed"));

        // Execute
        assertThrows(RuntimeException.class, () -> kafkaService.sendMessage(message));

        // Verify it was called
        verify(kafkaTemplate).send(TOPIC, message);
    }

    @Test
    void testSendMessage_whenKafkaSendFails_shouldLogError() {
        String message = "error";

        // Simula falha no envio Kafka
        when(kafkaTemplate.send(anyString(), anyString()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Kafka error")));

        assertDoesNotThrow(() -> kafkaService.sendMessage(message));

        verify(kafkaTemplate).send(TOPIC, message);
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

    @Test
    void testConsume_shouldCatchExceptionInAdd() {
        KafkaService service = Mockito.spy(new KafkaService());

        // Simulate an exception when calling addMessage
        doThrow(new RuntimeException("forced error")).when(service).processMessage(anyString());

        // Should not throw — just log error
        assertDoesNotThrow(() -> service.consume("test-message"));
    }

    @Test
    void testConsumeWithRetry() {
        KafkaService spyService = spy(kafkaService);

        // O método deve capturar exceção e não lançar
        assertDoesNotThrow(() -> spyService.consumeWithRetry("y"));
    }

    @Test
    void testConsumeWithRetry_exception() {
        KafkaService spyService = spy(kafkaService);

        // O método deve capturar exceção e não lançar
        assertDoesNotThrow(() -> spyService.consumeWithRetry("retry"));
    }

    @Test
    void testFetchMessagesFromKafka_returnsMessages() {
        @SuppressWarnings("unchecked")
        KafkaConsumer<String, String> mockConsumer = mock(KafkaConsumer.class);
        when(consumerFactory.createConsumer()).thenReturn(mockConsumer);

        ConsumerRecord<String, String> record1 = new ConsumerRecord<>(TOPIC, 0, 0L, "key1", "msg1");
        ConsumerRecord<String, String> record2 = new ConsumerRecord<>(TOPIC, 0, 1L, "key2", "msg2");
        ConsumerRecords<String, String> records = new ConsumerRecords<>(
                java.util.Map.of(
                        new org.apache.kafka.common.TopicPartition(TOPIC, 0),
                        List.of(record1, record2)
                )
        );

        // First poll returns records, second poll returns empty
        when(mockConsumer.poll(java.time.Duration.ZERO)).thenReturn(new ConsumerRecords<>(java.util.Collections.emptyMap()));
        when(mockConsumer.poll(java.time.Duration.ofSeconds(2)))
                .thenReturn(records)
                .thenReturn(new ConsumerRecords<>(java.util.Collections.emptyMap()));

        List<String> result = kafkaService.fetchMessagesFromKafka(2);

        assertEquals(List.of("msg1", "msg2"), result);
        verify(mockConsumer).subscribe(List.of(TOPIC));
        verify(mockConsumer, times(1)).poll(java.time.Duration.ZERO);
        verify(mockConsumer, atLeastOnce()).poll(java.time.Duration.ofSeconds(2));
        verify(mockConsumer, atLeastOnce()).commitSync();
        verify(mockConsumer).close();
    }

    @Test
    void testFetchMessagesFromKafka_returnsUpToMaxMessages() {
        @SuppressWarnings("unchecked")
        KafkaConsumer<String, String> mockConsumer = mock(KafkaConsumer.class);
        when(consumerFactory.createConsumer()).thenReturn(mockConsumer);

        ConsumerRecord<String, String> record1 = new ConsumerRecord<>(TOPIC, 0, 0L, "key1", "msg1");
        ConsumerRecord<String, String> record2 = new ConsumerRecord<>(TOPIC, 0, 1L, "key2", "msg2");
        ConsumerRecord<String, String> record3 = new ConsumerRecord<>(TOPIC, 0, 2L, "key3", "msg3");
        ConsumerRecords<String, String> records = new ConsumerRecords<>(
                java.util.Map.of(
                        new org.apache.kafka.common.TopicPartition(TOPIC, 0),
                        List.of(record1, record2, record3)
                )
        );

        when(mockConsumer.poll(java.time.Duration.ZERO)).thenReturn(new ConsumerRecords<>(java.util.Collections.emptyMap()));
        when(mockConsumer.poll(java.time.Duration.ofSeconds(2)))
                .thenReturn(records)
                .thenReturn(new ConsumerRecords<>(java.util.Collections.emptyMap()));

        List<String> result = kafkaService.fetchMessagesFromKafka(2);

        assertEquals(2, result.size());
        assertTrue(result.contains("msg1") || result.contains("msg2") || result.contains("msg3"));
        verify(mockConsumer).close();
    }

    @Test
    void testFetchMessagesFromKafka_handlesExceptionAndClosesConsumer() {
        @SuppressWarnings("unchecked")
        KafkaConsumer<String, String> mockConsumer = mock(KafkaConsumer.class);
        when(consumerFactory.createConsumer()).thenReturn(mockConsumer);

        when(mockConsumer.poll(java.time.Duration.ZERO)).thenThrow(new RuntimeException("poll error"));

        List<String> result = kafkaService.fetchMessagesFromKafka(1);

        assertTrue(result.isEmpty());
        verify(mockConsumer).close();
    }

    @Test
    void testFetchMessagesFromKafka_noMessages() {
        @SuppressWarnings("unchecked")
        KafkaConsumer<String, String> mockConsumer = mock(KafkaConsumer.class);
        when(consumerFactory.createConsumer()).thenReturn(mockConsumer);

        when(mockConsumer.poll(java.time.Duration.ZERO)).thenReturn(new ConsumerRecords<>(java.util.Collections.emptyMap()));
        when(mockConsumer.poll(java.time.Duration.ofSeconds(2))).thenReturn(new ConsumerRecords<>(java.util.Collections.emptyMap()));

        List<String> result = kafkaService.fetchMessagesFromKafka(5);

        assertTrue(result.isEmpty());
        verify(mockConsumer).close();
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