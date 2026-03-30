package com.example.demo.core.application.service.impl;

import com.example.demo.common.cache.core.CacheService;
import com.example.demo.core.application.cache.PermissionCache;
import com.example.demo.core.application.cache.RolePermissionCache;
import com.example.demo.core.application.dto.projection.PermissionProjection;
import com.example.demo.core.application.dto.response.PermissionCheckResponse;
import com.example.demo.core.application.dto.response.RolePermissionResponse;
import com.example.demo.core.application.service.PermissionService;
import com.example.demo.core.domain.helper.PermissionHelper;
import com.example.demo.core.persistence.AccountRoleRepository;
import com.example.demo.core.persistence.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {


    private final PermissionCache permissionCache;
    private final RolePermissionCache rolePermissionCache;

    private final RoleRepository roleRepository;
    private final AccountRoleRepository  accountRoleRepository;

    private final PermissionHelper permissionHelper;

    private final CacheService cacheService;

    @Transactional(readOnly = true)
    @Override
    public PermissionCheckResponse check(String accountId, String path, String method, String secret) {

        permissionHelper.validateInternalSecret(secret);
        String normalizedPath = permissionHelper.normalizePath(path);

        Optional<PermissionCheckResponse> cached =
                permissionCache.get(accountId, normalizedPath, method);

        if (cached.isPresent()) return cached.get();

        Integer bitmask = roleRepository.getMergedBitmask(accountId, normalizedPath, method);

        PermissionCheckResponse response =
                (bitmask == null || bitmask == 0)
                        ? PermissionCheckResponse.deny("NO_PERMISSION")
                        : permissionHelper.hasPermission(bitmask, method)
                        ? PermissionCheckResponse.allow("OK")
                        : PermissionCheckResponse.deny("NO_PERMISSION");

        String version = cacheService.getOrInitVersion(accountId);

        permissionCache.put(accountId, normalizedPath, method, version, response);

        return response;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RolePermissionResponse> getPermissionsByAccountId(String accId) {
        Optional<List<RolePermissionResponse>> cached = rolePermissionCache.get(accId);
        if (cached.isPresent()) return cached.get();

        List<PermissionProjection> daos = accountRoleRepository.getAllPermissions(accId);
        List<RolePermissionResponse> response = permissionHelper.buildRolePermission(daos);

        String version = cacheService.getOrInitVersion(accId);
        rolePermissionCache.put(accId, version, response);

        return response;

    }


}
