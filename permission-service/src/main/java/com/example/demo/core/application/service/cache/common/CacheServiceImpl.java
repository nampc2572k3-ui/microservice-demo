package com.example.demo.core.application.service.cache.common;

import com.example.demo.common.constant.cache.CacheKeyFactory;
import com.example.demo.common.constant.cache.CacheValue;
import com.example.demo.core.application.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheServiceImpl implements CacheService {

    private final CacheWarmupService cacheWarmupService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final long REFRESH_BEFORE_MS = Duration.ofMinutes(3).toMillis();

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, String accId, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) return Optional.empty();

            CacheValue<?> cache = (CacheValue<?>) value;
            long now = System.currentTimeMillis();

            //  refresh ahead
            if (cache.getExpireAt() - now <= REFRESH_BEFORE_MS) {
                cacheWarmupService.refreshAsync(accId);
            }

            return Optional.of((T) cache.getData());

        } catch (Exception ex) {
            log.warn("Redis read failed for key {}: {}", key, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public <T> void put(String key, T value, Duration ttl) {
        try {
            long jitter = ThreadLocalRandom.current().nextLong(0, 300_000);
            long finalTtl = ttl.toMillis() + jitter;

            long expireAt = System.currentTimeMillis() + finalTtl;
            CacheValue<T> cache = new CacheValue<>(value, expireAt);

            redisTemplate.opsForValue().set(key, cache, Duration.ofMillis(finalTtl));

        } catch (Exception ex) {
            log.warn("Redis write failed for key {}: {}", key, ex.getMessage());
        }
    }

    @Override
    public boolean tryLock(String key, Duration ttl) {
        try {
            return Boolean.TRUE.equals(
                    redisTemplate.opsForValue().setIfAbsent(key, "1", ttl)
            );
        } catch (Exception ex) {
            log.warn("Redis lock failed for key {}: {}", key, ex.getMessage());
            return false;
        }
    }

    @Override
    public String getOrInitVersion(String accId) {
        String key = CacheKeyFactory.version(accId);
        Object v = redisTemplate.opsForValue().get(key);

        if (v != null) return v.toString();

        String version = "v" + System.currentTimeMillis();
        redisTemplate.opsForValue().set(key, version);

        return version;
    }

    @Override
    public void setVersion(String accId, String version) {
        redisTemplate.opsForValue().set(CacheKeyFactory.version(accId), version);
    }
}
