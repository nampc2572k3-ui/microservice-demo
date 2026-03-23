package com.example.demo.core.adapter.controller;

import com.example.demo.common.utils.ResponseUtils;
import com.example.demo.core.application.dto.request.MenuRequest;
import com.example.demo.core.application.dto.request.common.PageRequest;
import com.example.demo.core.application.dto.response.MenuResponse;
import com.example.demo.core.application.dto.response.common.BaseResponse;
import com.example.demo.core.application.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<MenuResponse>>> getAll(
            @ModelAttribute @Valid PageRequest request
    ) {
        var page = menuService.getMenus(request);
        return ResponseEntity.ok(ResponseUtils.success(
                page.getContent(),
                "Get data successfully",
                page)
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> createMenu(
            @RequestBody @Valid MenuRequest request
    ) {
        menuService.createMenu(request);
        return ResponseEntity.ok(ResponseUtils.success("Create menu successfully"));
    }

    @PutMapping("/{mid}")
    public ResponseEntity<BaseResponse<Void>> updateMenu(
            @PathVariable Long mid,
            @RequestBody @Valid MenuRequest request
    ) {
        menuService.updateMenu(mid, request);
        return ResponseEntity.ok(ResponseUtils.success("Update menu successfully"));
    }

    @DeleteMapping("/{mid}")
    public ResponseEntity<BaseResponse<Void>> deleteMenu(
            @PathVariable Long mid
    ) {
        menuService.deleteMenu(mid);
        return ResponseEntity.ok(ResponseUtils.success("Delete menu successfully"));
    }

}
