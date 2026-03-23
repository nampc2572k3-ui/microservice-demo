package com.example.demo.core.application.service.impl;

import com.example.demo.core.domain.helper.PermissionHelper;
import com.example.demo.core.application.dto.response.PermissionCheckResponse;
import com.example.demo.core.domain.repository.RoleRepository;
import com.example.demo.core.application.service.CacheService;
import com.example.demo.core.application.service.PermissionService;
import com.example.demo.core.application.service.cache.PermissionCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {


    private final PermissionCacheService permissionCacheService;
    private final RoleRepository roleRepository;
    private final CacheService cacheService;


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


}
