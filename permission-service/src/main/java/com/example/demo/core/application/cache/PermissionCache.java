package com.example.demo.core.application.cache;

import com.example.demo.common.cache.core.CacheService;
import com.example.demo.common.cache.key.CacheKeyFactory;
import com.example.demo.common.cache.template.AbstractCacheService;
import com.example.demo.core.application.dto.response.PermissionCheckResponse;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class PermissionCache extends AbstractCacheService {

    private static final Duration TTL = Duration.ofMinutes(30);

    public PermissionCache(CacheService cacheService) {
        super(cacheService);
    }

    public Optional<PermissionCheckResponse> get(String accId, String path, String method) {
        String version = cacheService.getOrInitVersion(accId);
        String key = CacheKeyFactory.permissionCheck(accId, version, path, method);
        return getValue(key, accId).map(PermissionCheckResponse::fromObject);
    }

    public void put(String accId, String path, String method, String version, PermissionCheckResponse response) {
        putValue(CacheKeyFactory.permissionCheck(accId, version, path, method), response, TTL);
    }


}
