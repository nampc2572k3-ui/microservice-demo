package com.example.demo.domain.consumer;

import com.example.demo.domain.event.external.PermissionChangedEvent;
import com.example.demo.domain.service.cache.common.CacheWarmupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionChangedConsumer {

    private final CacheWarmupService cacheWarmupService;

    @KafkaListener(
            topics = "${app.kafka.topics.permission-changed}",
            groupId = "permission-service-cache-sync",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onPermissionChanged(
            PermissionChangedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received permission.changed for account {} [partition={}, offset={}]",
                event.getAccountId(), partition, offset);

        cacheWarmupService.refreshAsync(event.getAccountId());
    }

}