package com.example.demo.core.application.service.impl;

import com.example.demo.core.application.dto.projection.PermissionProjection;
import com.example.demo.core.application.dto.response.PermissionCheckResponse;
import com.example.demo.core.application.dto.response.RolePermissionResponse;
import com.example.demo.core.application.service.CacheService;
import com.example.demo.core.application.service.PermissionService;
import com.example.demo.core.application.service.cache.PermissionCacheService;
import com.example.demo.core.domain.helper.PermissionHelper;
import com.example.demo.core.persistence.AccountRoleRepository;
import com.example.demo.core.persistence.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {


    private final PermissionCacheService permissionCacheService;
    private final RoleRepository roleRepository;
    private final CacheService cacheService;
    private final AccountRoleRepository  accountRoleRepository;


    @Transactional(readOnly = true)
    @Override
    public PermissionCheckResponse check(String accountId, String path, String method, String secret) {

        PermissionHelper.validateInternalSecret(secret);
        String normalizedPath = PermissionHelper.normalizePath(path);

        Optional<PermissionCheckResponse> cached =
                permissionCacheService.get(accountId, normalizedPath, method);

        if (cached.isPresent()) return cached.get();

        Integer bitmask = roleRepository.getMergedBitmask(accountId, normalizedPath, method);

        PermissionCheckResponse response =
                (bitmask == null || bitmask == 0)
                        ? PermissionCheckResponse.deny("NO_PERMISSION")
                        : PermissionHelper.hasPermission(bitmask, method)
                        ? PermissionCheckResponse.allow("OK")
                        : PermissionCheckResponse.deny("NO_PERMISSION");

        String version = cacheService.getOrInitVersion(accountId);

        permissionCacheService.put(accountId, normalizedPath, method, version, response);

        return response;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RolePermissionResponse> getPermissionsByAccountId(String accId) {
        List<PermissionProjection> rows = accountRoleRepository.getAllPermissions(accId);

        // Map: roleName -> (menuCode -> permissions)
        Map<String, Map<String, Set<String>>> roleMap = new HashMap<>();

        for (PermissionProjection row : rows) {

            roleMap
                    .computeIfAbsent(row.getRoleName(), r -> new HashMap<>())
                    .computeIfAbsent(row.getMenuCode(), m -> new HashSet<>())
                    .addAll(PermissionHelper.mapBitmaskToPermissions(row.getBitmask()));
        }

        // Convert sang DTO
        return roleMap.entrySet().stream()
                .map(roleEntry -> {

                    List<RolePermissionResponse.MenuPermissionResponse> menus =
                            roleEntry.getValue().entrySet().stream()
                                    .map(menuEntry ->
                                            RolePermissionResponse.MenuPermissionResponse.builder()
                                                    .code(menuEntry.getKey())
                                                    .permissions(new ArrayList<>(menuEntry.getValue()))
                                                    .build()
                                    )
                                    .toList();

                    return RolePermissionResponse.builder()
                            .roleName(roleEntry.getKey())
                            .menus(menus)
                            .build();
                })
                .toList();
    }


}
