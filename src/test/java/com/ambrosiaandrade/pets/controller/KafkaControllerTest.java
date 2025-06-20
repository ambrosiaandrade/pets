package com.ambrosiaandrade.pets.controller;

import com.ambrosiaandrade.pets.service.KafkaService;
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
    private KafkaService kafkaService;

    @Nested
    class Kafka {

        @Test
        void kafkaProduce() throws Exception {
            doNothing().when(kafkaService).sendMessage(anyString());

            mockMvc.perform(
                            get("/kafka/producer")
                                    .param("msg", "Hello kafka")
                    )
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(kafkaService, times(1)).sendMessage(anyString());
        }

        @Test
        void kafkaConsume() throws Exception {
            when(kafkaService.getMessages()).thenReturn(List.of("Hello kafka", "test 1"));

            mockMvc.perform(
                            get("/kafka/consumer")
                    )
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(kafkaService, times(1)).getMessages();
        }

    }

}