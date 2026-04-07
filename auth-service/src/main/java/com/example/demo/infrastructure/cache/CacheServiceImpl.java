package com.example.demo.infrastructure.cache;

import com.example.demo.common.cache.core.CacheService;
import com.example.demo.common.cache.core.CacheValue;
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

    private final RedisTemplate<String, Object> redisTemplate;


    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) return Optional.empty();

            CacheValue<?> cache = (CacheValue<?>) value;

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

}
