package com.example.demo.domain.service;

import com.example.demo.domain.model.dto.request.AssignRoleRequest;
import com.example.demo.domain.model.dto.response.RoleResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface RoleService {

    List<RoleResponse> getRolesByAccount(String accId);

    void assignRole(String accId, @Valid AssignRoleRequest request);

    void revokeRole(String accId, Long roleId);
}
