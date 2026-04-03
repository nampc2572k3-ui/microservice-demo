package com.example.demo.core.application.cache;

import com.example.demo.common.cache.CacheKeyFactory;
import com.example.demo.core.domain.model.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenCache {

    private final RedisTemplate<String, Object> redisTemplate;;

    public void storeSession(String accId, RefreshToken session, long ttlSeconds) {
        redisTemplate.opsForValue()
                .set(
                        CacheKeyFactory.buildSessionKey(accId, session.getJti()),
                        session, Duration.ofMillis(ttlSeconds)
                );
    }

    public Optional<RefreshToken> getRefreshToken(String accId, String jti) {
        return redisTemplate.opsForValue().get(CacheKeyFactory.buildSessionKey(accId, jti));
    }

    private RefreshToken fromObject(Object object) {
        return null;
    }


}
