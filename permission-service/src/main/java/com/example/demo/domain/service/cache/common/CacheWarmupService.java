package com.example.demo.domain.service.cache.common;

import com.example.demo.common.constant.cache.CacheKeyFactory;
import com.example.demo.domain.helper.MenuHelper;
import com.example.demo.domain.model.dto.projection.ResourcePermissionProjection;
import com.example.demo.domain.model.dto.response.MenuTreeResponse;
import com.example.demo.domain.model.dto.response.PermissionCheckResponse;
import com.example.demo.domain.model.dto.response.RoleResponse;
import com.example.demo.domain.repository.MenuRepository;
import com.example.demo.domain.repository.ResourceRepository;
import com.example.demo.domain.repository.RoleRepository;
import com.example.demo.domain.service.CacheService;
import com.example.demo.domain.service.cache.MenuCacheService;
import com.example.demo.domain.service.cache.PermissionCacheService;
import com.example.demo.domain.service.cache.RoleCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheWarmupService {

    private final ResourceRepository resourceRepository;
    private final MenuRepository menuRepository;
    private final RoleRepository roleRepository;

    private final PermissionCacheService permissionCacheService;
    private final MenuCacheService menuCacheService;
    private final RoleCacheService roleCacheService;
    private final CacheService cacheService;

    private static final Duration LOCK_TTL = Duration.ofMinutes(2);

    @Async
    public void refreshAsync(String accId) {

        String lockKey = CacheKeyFactory.refreshLock(accId);

        if (!cacheService.tryLock(lockKey, LOCK_TTL)) {
            return;
        }

        try {
            log.info("Refreshing cache for account {}", accId);

            String newVersion = "v" + System.currentTimeMillis();

            warmup(accId, newVersion);

            cacheService.setVersion(accId, newVersion);

            log.info("Refresh done for {}", accId);

        } catch (Exception e) {
            log.error("Refresh failed: {}", e.getMessage());
        }
    }

    public void warmup(String accId, String version) {

        // permission
        resourceRepository.findAllByAccount(accId)
                .forEach(p -> permissionCacheService.put(
                        accId,
                        p.getPathPattern(),
                        p.getHttpMethod(),
                        version,
                        PermissionCheckResponse.from(p)
                ));

        // menu
        List<MenuTreeResponse> tree = MenuHelper.buildMenuTree(
                menuRepository.findMenuByAccount(accId)
        );
        menuCacheService.put(accId, version, tree);

        // roles
        List<RoleResponse> roles =
                roleRepository.findRolesByAccountId(accId);
        roleCacheService.put(accId, version, roles);
    }
}