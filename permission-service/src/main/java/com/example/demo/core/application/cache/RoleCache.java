package com.example.demo.core.application.cache;

import com.example.demo.common.cache.core.CacheService;
import com.example.demo.common.cache.key.CacheKeyFactory;
import com.example.demo.common.cache.template.AbstractCacheService;
import com.example.demo.core.application.dto.response.RoleResponse;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class RoleCache extends AbstractCacheService {

    private static final Duration TTL = Duration.ofMinutes(30);

    public RoleCache(CacheService cacheService) {
        super(cacheService);
    }

    public Optional<List<RoleResponse>> get(String accId) {
        String version = cacheService.getOrInitVersion(accId);
        String key = CacheKeyFactory.accountRoles(accId, version);
        return getValue(key, accId).map(RoleResponse::fromObject);
    }

    public void put(String accId, String version, List<RoleResponse> roles) {
        putValue(CacheKeyFactory.accountRoles(accId, version), roles, TTL);
    }

}
