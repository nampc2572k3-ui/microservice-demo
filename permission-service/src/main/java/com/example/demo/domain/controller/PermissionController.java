package com.example.demo.domain.controller;

import com.example.demo.common.utils.ResponseUtils;
import com.example.demo.domain.model.dto.response.common.BaseResponse;
import com.example.demo.domain.model.dto.response.MenuTreeResponse;
import com.example.demo.domain.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final MenuService menuService;

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
