package com.example.demo.core.adapter.controller;

import com.example.demo.common.utils.ResponseUtils;
import com.example.demo.core.application.dto.request.AssignMenuRequest;
import com.example.demo.core.application.dto.response.RolePermissionResponse;
import com.example.demo.core.application.dto.response.RoleResponse;
import com.example.demo.core.application.dto.response.common.BaseResponse;
import com.example.demo.core.application.service.MenuService;
import com.example.demo.core.application.service.PermissionService;
import com.example.demo.core.application.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final MenuService menuService;
    private final PermissionService permissionService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<RoleResponse>>> getAll() {
        var response = roleService.getAll();
        return ResponseEntity.ok(ResponseUtils.success(response, "Get data successfully"));
    }

    @PostMapping("/{rid}/menus")
    public ResponseEntity<BaseResponse<Void>> assignMenu(
            @PathVariable Long rid,
            @RequestBody @Valid AssignMenuRequest request
    ) {
        menuService.assignMenuToRole(rid, request);
        return ResponseEntity.ok(ResponseUtils.success("Assign menu successfully"));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<RolePermissionResponse>>> getPermissions(
            @PathVariable String accId) {
        var response = permissionService.getPermissionsByAccountId(accId);
        return ResponseEntity.ok(ResponseUtils.success(response, "Get data completed"));
    }
}
