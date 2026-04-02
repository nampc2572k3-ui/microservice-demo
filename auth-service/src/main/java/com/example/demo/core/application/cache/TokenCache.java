package com.example.demo.core.application.cache;

import com.example.demo.common.cache.CacheKeyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenCache {

    private final StringRedisTemplate redisTemplate;

    public void storeRefreshToken(String accId, String refreshToken, long expirationMillis) {
        redisTemplate.opsForValue()
                .set(
                        CacheKeyFactory.refreshToken(refreshToken) + accId,
                        refreshToken, Duration.ofMillis(expirationMillis)
                );
    }

    public String getRefreshToken(String accId, String refreshToken) {
        return redisTemplate.opsForValue().get(CacheKeyFactory.refreshToken(refreshToken) + accId);
    }



}
