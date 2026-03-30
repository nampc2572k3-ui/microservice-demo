package com.example.demo.core.application.cache;

import com.example.demo.common.cache.core.CacheService;
import com.example.demo.common.cache.key.CacheKeyFactory;
import com.example.demo.common.cache.template.AbstractCacheService;
import com.example.demo.core.application.dto.response.RolePermissionResponse;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class RolePermissionCache extends AbstractCacheService {

    private static final Duration TTL = Duration.ofMinutes(30);

    public RolePermissionCache(CacheService cacheService) {
        super(cacheService);
    }

    public Optional<List<RolePermissionResponse>> get(String accId) {
        String version = cacheService.getOrInitVersion(accId);
        String key = CacheKeyFactory.rolePermissions(accId, version);
        return getValue(key, accId).map(RolePermissionResponse::fromObject);
    }

    public void put(String accId, String version, List<RolePermissionResponse> rolePermissions) {
        putValue(CacheKeyFactory.rolePermissions(accId, version), rolePermissions, TTL);
    }

}