package com.example.demo.domain.service.impl;

import com.example.demo.domain.helper.PermissionHelper;
import com.example.demo.domain.model.dto.response.PermissionCheckResponse;
import com.example.demo.domain.repository.RoleRepository;
import com.example.demo.domain.service.PermissionService;
import com.example.demo.domain.service.cache.PermissionCacheService;
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


    @Override
    public PermissionCheckResponse check(String accountId, String path, String method, String secret) {
        PermissionHelper.validateInternalSecret(secret);

        String normalizedPath = PermissionHelper.normalizePath(path);

        Optional<PermissionCheckResponse> cached =
                permissionCacheService.getPermissionCheck(accountId, normalizedPath, method);
        if (cached.isPresent()) {
            return cached.get();
        }

        Integer mergedBitmask = roleRepository.getMergedBitmask(accountId, normalizedPath, method);
        log.info("Merged bitmask for accountId={}, path={}, method={} is {}", accountId, path, method, mergedBitmask);

        PermissionCheckResponse response;

        if (mergedBitmask == null || mergedBitmask == 0) {
            response = PermissionCheckResponse.deny("NO_PERMISSION");
        } else if (PermissionHelper.hasPermission(mergedBitmask, method)) {
            response = PermissionCheckResponse.allow("OK");
        } else {
            response = PermissionCheckResponse.deny("NO_PERMISSION");
        }

        permissionCacheService.putPermissionCheck(accountId, normalizedPath, method, response);

        return response;

    }


}
