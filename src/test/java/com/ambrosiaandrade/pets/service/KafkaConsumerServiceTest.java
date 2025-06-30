package com.ambrosiaandrade.pets.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.ConsumerFactory;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @Mock
    private ConsumerFactory<String, String> consumerFactory;
    
    @InjectMocks
    private KafkaConsumerService service;

    private static final String TOPIC = "unit-test-topic";

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        Field field = KafkaConsumerService.class.getDeclaredField("TOPIC");
        field.setAccessible(true);
        field.set(service, TOPIC);
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

        List<String> result = service.fetchMessagesFromKafka(2);

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

        List<String> result = service.fetchMessagesFromKafka(2);

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

        List<String> result = service.fetchMessagesFromKafka(1);

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

        List<String> result = service.fetchMessagesFromKafka(5);

        assertTrue(result.isEmpty());
        verify(mockConsumer).close();
    }

}