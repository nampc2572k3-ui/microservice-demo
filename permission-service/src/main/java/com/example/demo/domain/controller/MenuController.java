package com.example.demo.domain.controller;

import com.example.demo.common.utils.ResponseUtils;
import com.example.demo.domain.model.dto.response.BaseResponse;
import com.example.demo.domain.model.dto.response.MenuResponse;
import com.example.demo.domain.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<MenuResponse>>> getAll() {
        var response = menuService.getAll();
        return ResponseEntity.ok(ResponseUtils.success(response, "Get data successfully"));
    }

}
