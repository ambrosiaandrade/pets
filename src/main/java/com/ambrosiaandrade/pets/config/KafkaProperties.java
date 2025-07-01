package com.ambrosiaandrade.pets.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Data
@Profile("dev")
@Component
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaProperties {

    private String topic;
    private int retries;
    private int retryBackoffMs;
    private String retryPolicy;

}
