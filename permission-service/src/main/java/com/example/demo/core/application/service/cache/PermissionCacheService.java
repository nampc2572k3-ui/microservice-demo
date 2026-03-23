package com.example.demo.core.application.service.cache;

import com.example.demo.common.constant.cache.CacheKeyFactory;
import com.example.demo.core.application.dto.response.PermissionCheckResponse;
import com.example.demo.core.application.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionCacheService {

    private final CacheService cacheService;
    private static final Duration TTL = Duration.ofMinutes(30);

    public Optional<PermissionCheckResponse> get(
            String accId, String path, String method) {

        String version = cacheService.getOrInitVersion(accId);
        String key = CacheKeyFactory.permissionCheck(accId, version, path, method);

        return cacheService.get(key, accId, PermissionCheckResponse.class)
                .map(PermissionCheckResponse::fromObject);
    }

    public void put(
            String accId, String path, String method, String version,
            PermissionCheckResponse response) {

        cacheService.put(
                CacheKeyFactory.permissionCheck(accId, version, path, method),
                response,
                TTL
        );
    }
}
