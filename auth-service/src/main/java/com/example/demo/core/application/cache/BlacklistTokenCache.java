package com.example.demo.core.application.cache;

import com.example.demo.common.cache.core.CacheService;
import com.example.demo.common.cache.key.CacheKeyFactory;
import com.example.demo.common.cache.template.AbstractCacheService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class BlacklistTokenCache extends AbstractCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public BlacklistTokenCache(CacheService cacheService, RedisTemplate<String, Object> redisTemplate) {
        super(cacheService);
        this.redisTemplate = redisTemplate;
    }

    public Optional<String> get(String token) {
        String key =  CacheKeyFactory.blacklistToken(token);
        return getValue(key).map(val -> (String) val);
    }

    public void put(String token, long expirationMillis) {
        putValue(
                CacheKeyFactory.blacklistToken(token),
                "true",
                Duration.ofMillis(expirationMillis)
        );
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(CacheKeyFactory.blacklistToken(token))
        );
    }

}
