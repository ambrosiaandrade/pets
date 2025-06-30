package com.ambrosiaandrade.pets.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaProperties {

    private String topic;
    private int retries;
    private int retryBackoffMs;
    private String retryPolicy;

}
