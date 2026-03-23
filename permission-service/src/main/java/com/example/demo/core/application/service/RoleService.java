package com.example.demo.core.application.service;

import com.example.demo.core.application.dto.request.AssignRoleRequest;
import com.example.demo.core.application.dto.response.RoleResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface RoleService {

    List<RoleResponse> getRolesByAccount(String accId);

    void assignRole(String accId, @Valid AssignRoleRequest request);

    void revokeRole(String accId, Long roleId);

    List<RoleResponse> getAll();
}
