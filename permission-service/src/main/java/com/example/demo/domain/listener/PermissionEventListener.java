package com.example.demo.domain.listener;

import com.example.demo.domain.event.PermissionChangedEvent;
import com.example.demo.domain.service.cache.PermissionCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionEventListener {

    private final PermissionCacheService permissionCacheService;

    @EventListener
    public void onPermissionChanged(PermissionChangedEvent event) {
        log.info("Permission changed for account {}, evicting cache", event.accId());
        permissionCacheService.evictAccount(event.accId());
    }

}
