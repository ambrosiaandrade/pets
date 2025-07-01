package com.ambrosiaandrade.pets.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("dev")
@Component
public class DlqKafkaListener {

    @KafkaListener(topics = "${app.kafka.topic}.DLQ")
    public void consumeDlq(String message) {
        log.warn("[consumeDlq]: {}", message);
    }

}
