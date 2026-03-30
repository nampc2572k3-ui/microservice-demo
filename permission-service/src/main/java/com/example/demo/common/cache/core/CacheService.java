package com.example.demo.common.cache.core;

import java.time.Duration;
import java.util.Optional;

public interface CacheService {

    <T> Optional<T> get(String key, String accId, Class<T> clazz);

    <T> void put(String key, T value, Duration ttl);

    boolean tryLock(String key, Duration ttl);

    String getOrInitVersion(String accId);

    void setVersion(String accId, String version);
}
