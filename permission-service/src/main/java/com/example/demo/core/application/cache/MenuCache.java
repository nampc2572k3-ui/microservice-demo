package com.example.demo.core.application.cache;

import com.example.demo.common.cache.core.CacheService;
import com.example.demo.common.cache.key.CacheKeyFactory;
import com.example.demo.common.cache.template.AbstractCacheService;
import com.example.demo.core.application.dto.response.MenuTreeResponse;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class MenuCache extends AbstractCacheService {

    private static final Duration TTL = Duration.ofMinutes(30);

    public MenuCache(CacheService cacheService) {
        super(cacheService);
    }

    public Optional<List<MenuTreeResponse>> get(String accId) {
        String version = cacheService.getOrInitVersion(accId);
        String key = CacheKeyFactory.menuTree(accId, version);
        return getValue(key, accId).map(MenuTreeResponse::fromObject);
    }

    public void put(String accId, String version, List<MenuTreeResponse> tree) {
        putValue(CacheKeyFactory.menuTree(accId, version), tree, TTL);
    }

}