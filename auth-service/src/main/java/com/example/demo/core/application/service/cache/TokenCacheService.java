package com.example.demo.core.application.service.cache;

import com.example.demo.common.constant.CacheKeyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenCacheService {

    private final StringRedisTemplate redisTemplate;

    public void storeRefreshToken(String accId, String refreshToken, long expirationMillis) {
        redisTemplate.opsForValue()
                .set(
                        CacheKeyFactory.refreshToken(refreshToken) + accId,
                        refreshToken, Duration.ofMillis(expirationMillis)
                );
    }



}
