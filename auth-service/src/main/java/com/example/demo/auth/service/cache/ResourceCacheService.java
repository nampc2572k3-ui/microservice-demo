package com.example.demo.auth.service.cache;

import com.example.demo.auth.model.entity.Resource;
import com.example.demo.auth.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceCacheService {

    private final ResourceRepository resourceRepository;
    private final AntPathMatcher matcher = new AntPathMatcher();

    @Cacheable(value = "resourceCache", key = "'ALL'")
    public List<Resource> loadAllResources() {
        log.info("Loading all resources from DB...");
        return resourceRepository.findAll();
    }

    public Resource findMatch(String method, String uri) {

        List<Resource> resources = loadAllResources();

        Resource matched = resources.stream()
                .filter(r -> r.getHttpMethod().equalsIgnoreCase(method))
                .filter(r -> matcher.match(r.getPattern(), uri))
                .findFirst()
                .orElse(null);

        if (matched != null) {
            log.debug("Matched resource: {} {} -> {}",
                    method, uri, matched.getPattern());
        } else {
            log.debug("No resource matched for {} {}", method, uri);
        }

        return matched;
    }

    @CacheEvict(value = "resourceCache", allEntries = true)
    public void evictCache() {
        log.info("Resource cache cleared.");
    }
}
