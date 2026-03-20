package com.example.demo.domain.service.cache;

import com.example.demo.common.constant.cache.CacheKeyFactory;
import com.example.demo.domain.model.dto.response.MenuTreeResponse;
import com.example.demo.domain.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuCacheService {

    private final CacheService cacheService;
    private static final Duration TTL = Duration.ofMinutes(30);

    public Optional<List<MenuTreeResponse>> get(String accId) {
        String version = cacheService.getOrInitVersion(accId);
        String key = CacheKeyFactory.menuTree(accId, version);

        return cacheService.get(key, accId, List.class)
                .map(MenuTreeResponse::fromObject);
    }

    public void put(String accId, String version, List<MenuTreeResponse> tree) {
        cacheService.put(CacheKeyFactory.menuTree(accId, version), tree, TTL);
    }
}
