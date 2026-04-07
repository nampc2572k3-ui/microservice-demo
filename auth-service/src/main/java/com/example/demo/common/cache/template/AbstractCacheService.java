package com.example.demo.common.cache.template;

import com.example.demo.common.cache.core.CacheService;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class AbstractCacheService {

    protected final CacheService cacheService;

    @SuppressWarnings("unchecked")
    protected <T> Optional<T> getValue(String key) {
        return cacheService.get(key, Object.class).map(value -> (T) value);
    }

    protected <T> void putValue(String key, T value, Duration ttl) {
        cacheService.put(key, value, ttl);
    }


}
