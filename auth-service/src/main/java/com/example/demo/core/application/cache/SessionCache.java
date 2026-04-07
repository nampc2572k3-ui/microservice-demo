package com.example.demo.core.application.cache;

import com.example.demo.common.cache.core.CacheService;
import com.example.demo.common.cache.key.CacheKeyFactory;
import com.example.demo.common.cache.template.AbstractCacheService;
import com.example.demo.infrastructure.metadata.SessionMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class SessionCache extends AbstractCacheService {

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    public SessionCache(CacheService cacheService) {
        super(cacheService);
    }

    public Optional<SessionMetadata> get(String jti, String accId) {
        String key = CacheKeyFactory.session(jti, accId);
        return getValue(key).map(val -> (SessionMetadata) val);
    }

    public void put(
            String jti, String accId, String deviceId, String userAgent, String clientIp
    ) {
        SessionMetadata metadata = new SessionMetadata(deviceId, clientIp, userAgent);
        putValue(
                CacheKeyFactory.session(jti, accId),
                metadata,
                Duration.ofMillis(refreshTokenExpirationMs)
        );
    }

}
