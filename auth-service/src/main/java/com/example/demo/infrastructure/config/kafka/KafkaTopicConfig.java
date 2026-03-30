package com.example.demo.infrastructure.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.topics.login-success}")
    private String loginSuccessTopic;

    @Value("${app.kafka.topics.login-permission-warmup}")
    private String loginPermissionWarmupTopic;

    @Bean
    public NewTopic loginSuccessTopic() {
        return TopicBuilder.name(loginSuccessTopic)
                .partitions(3)
                .replicas(2)
                .build();
    }


    @Bean
    public NewTopic loginPermissionWarmupTopic() {
        return TopicBuilder.name(loginPermissionWarmupTopic)
                .partitions(3)
                .replicas(2)
                .build();
    }

}
