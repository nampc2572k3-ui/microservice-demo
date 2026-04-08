package com.example.demo.infrastructure.outbox;

import com.example.demo.core.domain.model.entity.OutboxEvent;
import com.example.demo.core.domain.model.enums.OutboxStatus;
import com.example.demo.core.persistence.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxWriter {

    private final OutboxEventRepository outboxEventRepository;

    private final ObjectMapper objectMapper;

    public void write(String topic, String key, Object payload) throws JsonProcessingException {
            OutboxEvent event = OutboxEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .topic(topic)
                    .payloadKey(key)
                    .payload(objectMapper.writeValueAsString(payload))
                    .status(OutboxStatus.PENDING)
                    .retryCount(0)
                    .createdAt(LocalDateTime.now())
                    .build();
            outboxEventRepository.save(event);
    }

}
