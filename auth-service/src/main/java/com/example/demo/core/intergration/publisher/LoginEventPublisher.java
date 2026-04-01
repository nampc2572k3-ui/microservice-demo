package com.example.demo.core.intergration.publisher;

import com.example.demo.core.domain.event.external.LoginPermissionWarmupEvent;
import com.example.demo.core.domain.model.entity.Account;
import com.example.demo.infrastructure.outbox.OutboxWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final OutboxWriter outboxWriter;

    @Value("${app.kafka.topics.login-permission-warmup}")
    private String loginPermissionWarmupTopic;

    public void publishLoginSuccess(Account acc) throws JsonProcessingException {
        outboxWriter.write(
                loginPermissionWarmupTopic,
                acc.getId(),
                LoginPermissionWarmupEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .accountId(acc.getId())
                        .build()
        );
    }


}
