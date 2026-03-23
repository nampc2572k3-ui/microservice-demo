package com.example.demo.core.application.service.cache;

import com.example.demo.common.constant.cache.CacheKeyFactory;
import com.example.demo.core.application.dto.response.RoleResponse;
import com.example.demo.core.application.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleCacheService {

    private final CacheService cacheService;
    private static final Duration TTL = Duration.ofMinutes(30);

    public Optional<List<RoleResponse>> get(String accId) {
        String version = cacheService.getOrInitVersion(accId);
        String key = CacheKeyFactory.accountRoles(accId, version);

        return cacheService.get(key, accId, List.class)
                .map(RoleResponse::fromObject);
    }

    public void put(String accId, String version, List<RoleResponse> roles) {
        cacheService.put(
                CacheKeyFactory.accountRoles(accId, version),
                roles,
                TTL
        );
    }
}
