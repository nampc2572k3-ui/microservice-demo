package com.example.demo.core.application.service;

import com.example.demo.core.application.dto.response.PermissionCheckResponse;
import com.example.demo.core.application.dto.response.RolePermissionResponse;

import java.util.List;

public interface PermissionService {
    PermissionCheckResponse check(String accountId, String path, String method, String secret);

    List<RolePermissionResponse> getPermissionsByAccountId(String accId);
}
