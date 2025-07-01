
package com.ambrosiaandrade.pets.service;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ConsumerFactory<String, String> consumerFactory;

    @Mock
    private RetryTemplate retryTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    private static final String TOPIC = "unit-test-topic";

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        kafkaTemplate = mock(KafkaTemplate.class);
        retryTemplate = RetryTemplate.builder().maxAttempts(3).fixedBackoff(10).build();
        kafkaProducerService = new KafkaProducerService(retryTemplate, kafkaTemplate);

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

        SendResult<String, String> sendResult = mock(SendResult.class);
        RecordMetadata metadata = mock(RecordMetadata.class);
        when(sendResult.getRecordMetadata()).thenReturn(metadata);

        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(sendResult);

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

    @Test
    void testSendMessageWithRetry() {
        String message = "Hello Kafka";

        SendResult<String, String> sendResult = mock(SendResult.class);
        RecordMetadata metadata = mock(RecordMetadata.class);
        when(sendResult.getRecordMetadata()).thenReturn(metadata);

        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.complete(sendResult);

        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(future);

        @SuppressWarnings("unchecked")
        RetryCallback<Void, RuntimeException> retryCallback = mock(RetryCallback.class);
        @SuppressWarnings("unchecked")
        RecoveryCallback<Void> recoveryCallback = mock(RecoveryCallback.class);

        // Corrigido: mocking correto do RetryTemplate
        lenient().when(retryTemplate.execute(retryCallback, recoveryCallback))
                .thenAnswer(invocation -> {
                    RetryCallback<Void, RuntimeException> callback = invocation.getArgument(0);
                    return callback.doWithRetry(mock(RetryContext.class));
                });

        kafkaProducerService.send(message, true); // ou sendMessageWithRetry se estiver exposto

        verify(kafkaTemplate).send(eq(TOPIC), eq(message));
    }

    @Test
    void testSendMessageWithRetry_exception() throws Exception {
        // Arrange: Mocka o .send().get() para lançar uma exceção
        CompletableFuture<SendResult<String, String>> future = mock(CompletableFuture.class);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(future);
        when(future.get()).thenThrow(new ExecutionException("Erro simulado", new RuntimeException("Erro Kafka")));

        // Act: Executa o método que deve cair no retry e recovery
        kafkaProducerService.send("message-unit-test", true);

        // Assert: verifica que o send foi chamado 3 vezes (retry)
        verify(kafkaTemplate, times(3)).send(anyString(), anyString());
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