package com.example.demo.common.cache.core;

import java.time.Duration;
import java.util.Optional;

public interface CacheService {

    <T> Optional<T> get(String key, Class<T> clazz);

    <T> void put(String key, T value, Duration ttl);

}
