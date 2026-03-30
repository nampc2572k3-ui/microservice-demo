package com.example.demo.common.cache.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheValue<T> {

    private T data;
    private long expireAt;

}
