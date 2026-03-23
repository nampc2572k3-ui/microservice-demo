package com.example.demo.core.application.service;

import com.example.demo.core.application.dto.response.PermissionCheckResponse;

public interface PermissionService {
    PermissionCheckResponse check(String accountId, String path, String method, String secret);
}
