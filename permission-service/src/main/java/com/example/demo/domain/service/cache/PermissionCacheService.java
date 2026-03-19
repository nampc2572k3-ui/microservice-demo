package com.example.demo.domain.service.cache;

import com.example.demo.common.constant.CacheKeyFactory;
import com.example.demo.domain.model.dto.projection.ResourcePermissionProjection;
import com.example.demo.domain.model.dto.response.MenuTreeResponse;
import com.example.demo.domain.model.dto.response.PermissionCheckResponse;
import com.example.demo.domain.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final Duration PERMISSION_TTL = Duration.ofMinutes(30);
    private static final Duration MENU_TREE_TTL = Duration.ofMinutes(30);

    private final ResourceRepository resourceRepository;



    public Optional<PermissionCheckResponse> getPermissionCheck(
            String accId, String path, String method) {
        String key = CacheKeyFactory.permissionCheck(accId, path, method);
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) return Optional.empty();

            PermissionCheckResponse response = PermissionCheckResponse.fromObject(value);
            return Optional.of(response);
        } catch (Exception ex) {
            log.warn("Redis read failed for permission check, fallback to DB: {}", ex.getMessage());
            return Optional.empty();
        }
    }


    public void putPermissionCheck(
            String accountId, String path, String method,
            PermissionCheckResponse response) {
        String key = CacheKeyFactory.permissionCheck(accountId, path, method);
        try {
            redisTemplate.opsForValue().set(key, response, PERMISSION_TTL);
        } catch (Exception e) {
            log.warn("Redis write failed for permission check: {}", e.getMessage());
        }
    }

    public void putMenuTree(String accountId, List<MenuTreeResponse> tree) {
        String key = CacheKeyFactory.menuTree(accountId);
        try {
            redisTemplate.opsForValue().set(key, tree, MENU_TREE_TTL);
        } catch (Exception e) {
            log.warn("Redis write failed for menu tree: {}", e.getMessage());
        }
    }


    public Optional<List<MenuTreeResponse>> getMenuTree(String accId) {
        String key = CacheKeyFactory.menuTree(accId);
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) return Optional.empty();

            List<MenuTreeResponse> tree = MenuTreeResponse.fromObject(value);
            return Optional.of(tree);
        } catch (Exception e) {
            log.warn("Redis read failed for menu tree: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void evictAccount(String accId) {
        try {
            String pattern = CacheKeyFactory.permissionCheckPattern(accId);
            Set<String> keys = redisTemplate.keys(pattern);

            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("Evicted {} permission cache keys for account {}", keys.size(), accId);

                redisTemplate.delete(CacheKeyFactory.menuTree(accId));

                redisTemplate.delete(CacheKeyFactory.accountRoles(accId));


            }
        } catch (Exception e) {
            log.warn("Redis eviction failed for account {}: {}", accId, e.getMessage());
        }
    }


    public void warmupCache(String accId) {
        List<ResourcePermissionProjection> perms = resourceRepository.findAllByAccount(accId);
        perms.forEach(p -> putPermissionCheck(accId, p.getPathPattern(), p.getHttpMethod(), PermissionCheckResponse.from(p)));
        log.info("Warmup cache completed for account {}", accId);
    }

}
