package com.ambrosiaandrade.pets.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class MyKafkaListenerTest {

    @InjectMocks
    private MyKafkaListener listener;

    @Test
    void testConsumeAddsMessage() {
        String message = "Consumed message";
        listener.consume(message);

        List<String> messages = listener.getMessages();
        assertEquals(1, messages.size());
        assertEquals(message, messages.get(0));
    }

    @Test
    void testGetMessagesReturnsAllConsumedMessages() {
        listener.consume("msg1");
        listener.consume("msg2");

        List<String> messages = listener.getMessages();
        assertEquals(2, messages.size());
        assertTrue(messages.contains("msg1"));
        assertTrue(messages.contains("msg2"));
    }

    @Test
    void testConsumeWithRetry() {
        MyKafkaListener spylistener = spy(listener);

        // O método deve capturar exceção e não lançar
        assertDoesNotThrow(() -> spylistener.consumeWithRetry("y"));
    }

    @Test
    void testConsumeWithRetry_exception() {
        MyKafkaListener spylistener = spy(listener);

        // O método deve capturar exceção e não lançar
        assertDoesNotThrow(() -> spylistener.consumeWithRetry("retry"));
    }

}