package com.example.demo.infrastructure.context;

import java.io.Serializable;

public record ClientInfoContext(
        String deviceId,
        String clientIp,
        String userAgent
) implements Serializable {}
