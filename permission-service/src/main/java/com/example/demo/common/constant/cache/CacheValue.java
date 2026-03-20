package com.example.demo.common.constant.cache;

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
