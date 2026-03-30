package com.example.demo.core.adapter.message.consumer;

import com.example.demo.core.application.facade.CacheWarmupFacade;
import com.example.demo.core.domain.event.external.PermissionChangedEvent;

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

    private final CacheWarmupFacade cacheWarmupFacade;

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

        cacheWarmupFacade.refresh(event.getAccountId());
    }

}