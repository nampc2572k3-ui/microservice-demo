package com.example.demo.core.application.cache;

import com.example.demo.common.cache.CacheKeyFactory;
import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomBusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class LoginRateLimitCache {

    private final StringRedisTemplate redisTemplate;

    private static  final int LOGIN_ATTEMPT_LIMIT = 5;
    private static final Duration TTL = Duration.ofMinutes(5);


    public void check(String ip, String username) {
        checkLoginRateLimitKey(CacheKeyFactory.buildIpKey(ip));
        checkLoginRateLimitKey(CacheKeyFactory.buildUserKey(username));
        checkLoginRateLimitKey(CacheKeyFactory.buildIpUserKey(ip, username));
    }

    private void checkLoginRateLimitKey(String key) {
        String val = redisTemplate.opsForValue().get(key);
        if (val != null && Integer.parseInt(val) >= LOGIN_ATTEMPT_LIMIT) {
            throw new CustomBusinessException(
                    ErrorCode.SPAM_LOGIN_ATTEMPTS.getCode(),
                    ErrorCode.SPAM_LOGIN_ATTEMPTS.getMessage()
            );
        }
    }

    private void incrementLoginFailNo(String key) {
        Long count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            redisTemplate.expire(key, TTL);
        }
    }

    public void loginSuccess(String ip, String username) {
        redisTemplate.delete(CacheKeyFactory.buildIpKey(ip));
        redisTemplate.delete(CacheKeyFactory.buildUserKey(username));
        redisTemplate.delete(CacheKeyFactory.buildIpUserKey(ip, username));
    }

    public void loginFailed(String ip, String username) {
        incrementLoginFailNo(CacheKeyFactory.buildIpKey(ip));
        incrementLoginFailNo(CacheKeyFactory.buildUserKey(username));
        incrementLoginFailNo(CacheKeyFactory.buildIpUserKey(ip, username));
    }

}
