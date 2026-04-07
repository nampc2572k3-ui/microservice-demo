package com.example.demo.infrastructure.metadata;

import java.io.Serializable;

public record SessionMetadata(
        String deviceId,
        String clientIp,
        String userAgent
) implements Serializable {}
