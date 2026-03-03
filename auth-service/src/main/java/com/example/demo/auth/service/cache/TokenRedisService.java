package com.example.demo.auth.service.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenRedisService {

    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX_BLACKLIST = "microservice:auth:blacklist-token:";
    private static final String PREFIX_REFRESH = "microservice:auth:refresh-token:";

    public void storeBlacklistToken(String token, long expirationMillis) {
        redisTemplate.opsForValue()
                .set(PREFIX_BLACKLIST + token, "true", Duration.ofMillis(expirationMillis));
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(PREFIX_BLACKLIST + token)
        );
    }

    public void storeRefreshToken(String userId, String refreshToken, long expirationMillis) {
        redisTemplate.opsForValue()
                .set(PREFIX_REFRESH + userId, refreshToken, Duration.ofMillis(expirationMillis));
    }

    public String getRefreshToken(String userId) {
        return redisTemplate.opsForValue().get(PREFIX_REFRESH + userId);
    }
}
