package com.example.demo.core.intergration.publisher;

import com.example.demo.core.domain.event.external.RegisterSuccessEvent;
import com.example.demo.core.domain.model.entity.Account;
import com.example.demo.infrastructure.outbox.OutboxWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterEventPublisher {

    private final OutboxWriter outboxWriter;

    @Value(("${app.kafja.topics.auth.register.success}"))
    private String registerSuccessTopic;

    public void publishRegisterSuccess(Account acc) throws JsonProcessingException {
        outboxWriter.write(
                registerSuccessTopic,
                acc.getId(),
                RegisterSuccessEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .accountId(acc.getId())
                        .username(acc.getUsername())
                        .build()
        );
    }

}
