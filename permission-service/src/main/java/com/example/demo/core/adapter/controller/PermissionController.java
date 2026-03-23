package com.example.demo.core.adapter.controller;

import com.example.demo.common.utils.ResponseUtils;
import com.example.demo.core.application.dto.response.PermissionCheckResponse;
import com.example.demo.core.application.dto.response.common.BaseResponse;
import com.example.demo.core.application.dto.response.MenuTreeResponse;
import com.example.demo.core.application.service.MenuService;
import com.example.demo.core.application.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final MenuService menuService;

    private final PermissionService permissionService;


    @GetMapping("/check")
    public ResponseEntity<BaseResponse<PermissionCheckResponse>> checkPermissions(
            @RequestParam String accountId,
            @RequestParam String path,
            @RequestParam String method,
            @RequestHeader(value = "X-Internal-Secret", required = false) String secret
    ) {
        var response = permissionService.check(accountId, path, method, secret);
        return ResponseEntity.ok(ResponseUtils.success(response, "Permission check completed"));
    }

    @GetMapping("/accounts/{accId}/menus")
    public ResponseEntity<BaseResponse<List<MenuTreeResponse>>> getMenusByAccount(
            @PathVariable String accId) {
        var response = menuService.getMenuTreeByAccount(accId);
        return ResponseEntity.ok(ResponseUtils.success(response, "Get data successfully"));
    }

    @GetMapping("/me/menus")
    public ResponseEntity<BaseResponse<List<MenuTreeResponse>>> getMyMenus(
            @RequestHeader("X-Account-Id") String accId
    ) {
        var response = menuService.getMenuTreeByAccount(accId);
        return ResponseEntity.ok(ResponseUtils.success(response, "Get data successfully"));
    }



}
