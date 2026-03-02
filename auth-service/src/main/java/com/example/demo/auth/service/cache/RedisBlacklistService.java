package com.example.demo.auth.service.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisBlacklistService {

    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "microservice:auth:blacklist-token:";

    public void blacklistToken(String token, long expirationMillis) {
        redisTemplate.opsForValue()
                .set(PREFIX + token, "true", Duration.ofMillis(expirationMillis));
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(PREFIX + token)
        );
    }
}
