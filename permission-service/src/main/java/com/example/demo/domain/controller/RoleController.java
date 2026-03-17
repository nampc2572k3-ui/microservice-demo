package com.example.demo.domain.controller;

import com.example.demo.common.utils.ResponseUtils;
import com.example.demo.domain.model.dto.request.AssignMenuRequest;
import com.example.demo.domain.model.dto.response.RoleResponse;
import com.example.demo.domain.model.dto.response.common.BaseResponse;
import com.example.demo.domain.service.MenuService;
import com.example.demo.domain.service.RoleService;
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


}
