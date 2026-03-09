package com.example.demo.auth.service.cache;

import com.example.demo.auth.model.entity.Resource;
import com.example.demo.auth.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceCacheService {

    private final ResourceRepository resourceRepository;

    @Cacheable(
            cacheManager = "caffeineCacheManager",
            value = "resourceCache",
            key = "'ALL'"
    )
    public List<Resource> loadAllResources() {
        log.info("Loading all resources from DB...");
        return resourceRepository.findAll();
    }


    @CacheEvict(
            cacheManager = "caffeineCacheManager",
            value = "resourceCache",
            allEntries = true
    )
    public void evictCache() {
        log.info("Resource cache cleared.");
    }
}
