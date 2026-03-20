package com.example.demo.domain.listener;

import com.example.demo.domain.event.PermissionChangedEvent;
import com.example.demo.domain.service.cache.common.CacheWarmupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionEventListener {

    private final CacheWarmupService cacheWarmupService;


    @EventListener
    public void onPermissionChanged(PermissionChangedEvent event) {

        String accId = event.accId();

        log.info("Permission changed for account {}, triggering refresh", accId);

        cacheWarmupService.refreshAsync(accId);
    }

}
