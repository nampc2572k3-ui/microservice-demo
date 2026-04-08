package com.example.demo.common.utils;

import com.example.demo.infrastructure.context.ClientInfoContext;
import jakarta.servlet.http.HttpServletRequest;

public class ClientInfoUtils {

    public static String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

    public static ClientInfoContext extract(HttpServletRequest request) {
        String deviceId = request.getHeader("X-Device-Id");

        String userAgent = request.getHeader("User-Agent");

        String clientIp = getClientIp(request);

        return new ClientInfoContext(deviceId, clientIp, userAgent);
    }

}
