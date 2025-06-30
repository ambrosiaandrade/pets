package com.ambrosiaandrade.pets.controller;

import com.ambrosiaandrade.pets.listener.MyKafkaListener;
import com.ambrosiaandrade.pets.service.KafkaConsumerService;
import com.ambrosiaandrade.pets.service.KafkaProducerService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KafkaController.class)
class KafkaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KafkaProducerService kafkaProducerService;

    @MockitoBean
    private KafkaConsumerService kafkaConsumerService;

    @MockitoBean
    private MyKafkaListener listener;

    @Nested
    class Kafka {

        @Test
        void kafkaProduce() throws Exception {
            doNothing().when(kafkaProducerService).send(anyString(), anyBoolean());

            mockMvc.perform(
                            get("/kafka/producer")
                                    .param("msg", "Hello kafka")
                    )
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(kafkaProducerService, times(1)).send(anyString(), anyBoolean());
        }

        @Test
        void kafkaConsume() throws Exception {
            when(listener.getMessages()).thenReturn(List.of("Hello kafka", "test 1"));

            mockMvc.perform(
                            get("/kafka/consumer")
                    )
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(listener, times(1)).getMessages();
        }

    }

}