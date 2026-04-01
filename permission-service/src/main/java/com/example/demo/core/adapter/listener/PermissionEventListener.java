package com.example.demo.core.adapter.listener;

import com.example.demo.core.application.facade.CacheWarmupFacade;
import com.example.demo.core.domain.event.internal.PermissionChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionEventListener {

    private final CacheWarmupFacade cacheWarmupFacade;

    @EventListener
    public void onPermissionChanged(PermissionChangedEvent event) {

        log.info("Permission changed for account {}, refresh cache", event.getAccountId());

        cacheWarmupFacade.refresh(event.getAccountId());

    }

}
