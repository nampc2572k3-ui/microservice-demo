package com.example.demo.core.config.cache;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CacheManagerConfig {

    /**
     * Create a primary CompositeCacheManager that attempts caches in order:
     * 1) Caffeine (for 'resourceCache')
     * 2) Redis (for other caches)
     *
     * This prevents the two cache managers from "stepping on" each other.
     */
    @Bean
    @Primary
    public CacheManager cacheManager(
            @Qualifier("redisCacheManager") CacheManager redisCacheManager) {

        CompositeCacheManager composite = new CompositeCacheManager();

        // Order matters: try caffeine first (so resourceCache is served locally), then redis
        List<CacheManager> delegates = new ArrayList<>();
        delegates.add(redisCacheManager);

        composite.setCacheManagers(delegates);
        // When a cache is not found on any delegate, let it fall through (do not create a no-op cache)
        composite.setFallbackToNoOpCache(false);

        return composite;
    }
}
