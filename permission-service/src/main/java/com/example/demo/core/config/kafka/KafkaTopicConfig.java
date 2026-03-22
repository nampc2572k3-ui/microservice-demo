package com.example.demo.core.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.topics.permission-changed}")
    private String permissionChangedTopic;

    @Value("${app.kafka.topics.role-assigned}")
    private String roleAssignedTopic;

    @Value("${app.kafka.topics.role-revoked}")
    private String roleRevokedTopic;

    @Bean
    public NewTopic permissionChangedTopic() {
        return TopicBuilder.name(permissionChangedTopic)
                .partitions(3)
                .replicas(2)
                .build();
    }

    @Bean
    public NewTopic roleAssignedTopic() {
        return TopicBuilder.name(roleAssignedTopic)
                .partitions(3)
                .replicas(2)
                .build();
    }

    @Bean
    public NewTopic roleRevokedTopic() {
        return TopicBuilder.name(roleRevokedTopic)
                .partitions(3)
                .replicas(2)
                .build();
    }
}
