package com.example.demo.core.adapter.controller;

import com.example.demo.common.utils.ResponseUtils;
import com.example.demo.core.application.dto.request.AssignRoleRequest;
import com.example.demo.core.application.dto.response.common.BaseResponse;
import com.example.demo.core.application.dto.response.RoleResponse;
import com.example.demo.core.application.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final RoleService roleService;

    @GetMapping("/{accId}/roles")
    public ResponseEntity<BaseResponse<List<RoleResponse>>> getRoles(@PathVariable("accId") String accId) {
        var response = roleService.getRolesByAccount(accId);
        return ResponseEntity.ok(ResponseUtils.success(response, "Get data successfully"));
    }

    @PostMapping("/{accId}/roles")
    public ResponseEntity<BaseResponse<Void>> assignRoles(
            @PathVariable String accId,
            @RequestBody @Valid AssignRoleRequest request
    ) {
        roleService.assignRole(accId, request);
        return ResponseEntity.ok(ResponseUtils.success("Assign role successfully"));
    }

    @DeleteMapping("/{accId}/roles/{roleId}")
    public ResponseEntity<BaseResponse<Void>> revokeRole(
            @PathVariable String accId,
            @PathVariable Long roleId) {
        roleService.revokeRole(accId, roleId);
        return ResponseEntity.ok(ResponseUtils.success("Revoke role successfully"));
    }

}
