package com.example.demo.domain.service;

import java.time.Duration;
import java.util.Optional;

public interface CacheService {
    <T> Optional<T> get(String key, String accId, Class<T> clazz);

    <T> void put(String key, T value, Duration ttl);

    boolean tryLock(String key, Duration ttl);

    void setVersion(String accId, String version);

    String getOrInitVersion(String accId);
}
