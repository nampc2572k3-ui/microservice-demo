package com.example.demo.infrastructure.outbox;

import com.example.demo.core.domain.model.entity.OutboxEvent;
import com.example.demo.core.domain.model.enums.OutboxStatus;
import com.example.demo.core.persistence.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxRelayScheduler {

    private static final int BATCH_SIZE = 100;
    private static final int MAX_RETRY = 5;

    private final OutboxEventRepository outboxEventRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5_000)
    @Transactional(readOnly = true)
    public void relay() {
        List<OutboxEvent> events = outboxEventRepository.findPendingWithLock(BATCH_SIZE);

        for (OutboxEvent event : events) {
            try {
                Object payload = objectMapper.readValue(event.getPayload(), Object.class);
                kafkaTemplate.send(event.getTopic(), event.getPayloadKey(), payload).get();
                event.setStatus(OutboxStatus.SENT);
                event.setSentAt(LocalDateTime.now());
            } catch (Exception ex) {
                int retries = event.getRetryCount() + 1;
                event.setRetryCount(retries);
                if (retries >= MAX_RETRY) {
                    event.setStatus(OutboxStatus.FAILED);
                    log.error("Outbox event {} permanently failed after {} retries", event.getEventId(), retries);
                } else {
                    log.warn("Outbox event {} failed, retry {}/{}", event.getEventId(), retries, MAX_RETRY);
                }
            }
        }
    }


}
