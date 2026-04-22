package com.example.demo.infrastructure.outbox;

import com.example.demo.core.domain.model.entity.OutboxEvent;
import com.example.demo.core.persistence.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxRelayScheduler {

    private static final int BATCH_SIZE = 100;

    private final OutboxEventRepository outboxEventRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5_000)
    @Transactional(rollbackFor = Exception.class)
    public void relay() {
        LocalDateTime now = LocalDateTime.now();
        List<OutboxEvent> events = outboxEventRepository.findPendingWithLock(BATCH_SIZE);

        if (events.isEmpty()) {
            return;
        }

        List<Long> sentIds = new ArrayList<>();

        for (OutboxEvent event : events) {
            try {
                Object payload = objectMapper.readValue(event.getPayload(), Object.class);
                kafkaTemplate.send(event.getTopic(), event.getPayloadKey(), payload).get();

                sentIds.add(event.getId());

            } catch (Exception ex) {
                log.warn(
                        "[OUTBOX] Kafka fail. Event {} sẽ retry sau 5s",
                        event.getEventId(),
                        ex
                );
            }
        }

        outboxEventRepository.markAsSent(sentIds, now);
    }

}
