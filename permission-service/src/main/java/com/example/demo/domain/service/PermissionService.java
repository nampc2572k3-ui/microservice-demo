package com.example.demo.domain.service;

import com.example.demo.domain.model.dto.response.PermissionCheckResponse;

public interface PermissionService {
    PermissionCheckResponse check(String accountId, String path, String method, String secret);
}
