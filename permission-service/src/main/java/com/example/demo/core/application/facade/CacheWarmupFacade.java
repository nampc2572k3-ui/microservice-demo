package com.example.demo.core.application.facade;

import com.example.demo.common.cache.core.CacheService;
import com.example.demo.common.cache.key.CacheKeyFactory;
import com.example.demo.core.application.cache.MenuCache;
import com.example.demo.core.application.cache.PermissionCache;
import com.example.demo.core.application.cache.RoleCache;
import com.example.demo.core.application.cache.RolePermissionCache;
import com.example.demo.core.application.dto.response.MenuTreeResponse;
import com.example.demo.core.application.dto.response.RolePermissionResponse;
import com.example.demo.core.application.dto.response.RoleResponse;
import com.example.demo.core.domain.helper.MenuHelper;
import com.example.demo.core.domain.helper.PermissionHelper;
import com.example.demo.core.persistence.AccountRoleRepository;
import com.example.demo.core.persistence.MenuRepository;
import com.example.demo.core.persistence.ResourceRepository;
import com.example.demo.core.persistence.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CacheWarmupFacade {

    private final ResourceRepository resourceRepository;
    private final MenuRepository menuRepository;
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;

    private final MenuCache menuCache;
    private final PermissionCache permissionCache;
    private final RoleCache roleCache;
    private final RolePermissionCache rolePermissionCache;

    private final MenuHelper menuHelper;
    private final PermissionHelper permissionHelper;

    private final CacheService cacheService;

    @Async
    public void refresh(String accId) {
        String lockKey = CacheKeyFactory.refreshLock(accId);

        if (!cacheService.tryLock(lockKey, Duration.ofMinutes(2))) {
            return;
        }

        String version = "v" + System.currentTimeMillis();

        // permission
        resourceRepository.findAllByAccount(accId)
                .forEach(p -> permissionCache.put(
                        accId,
                        p.getPathPattern(),
                        p.getHttpMethod(),
                        version,
                        permissionHelper.buiPermissionCheckResponse(p)
                ));

        // menu
        List<MenuTreeResponse> tree = menuHelper.buildMenuTree(
                menuRepository.findMenuByAccount(accId)
        );
        menuCache.put(accId, version, tree);

        // roles
        List<RoleResponse> roles = roleRepository.findRolesByAccountId(accId);
        roleCache.put(accId, version, roles);

        // role-permission
        List<RolePermissionResponse> rolePermissions =
                permissionHelper.buildRolePermission(
                        accountRoleRepository.getAllPermissions(accId)
                );
        rolePermissionCache.put(accId, version, rolePermissions);
    }
}
