package com.example.demo.core.application.service;

import com.example.demo.core.application.dto.request.AssignMenuRequest;
import com.example.demo.core.application.dto.request.MenuRequest;
import com.example.demo.core.application.dto.request.common.PageRequest;
import com.example.demo.core.application.dto.response.MenuResponse;
import com.example.demo.core.application.dto.response.MenuTreeResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MenuService {

    List<MenuTreeResponse> getMenuTreeByAccount(String accId);

    Page<MenuResponse> getMenus(PageRequest request);

    void createMenu(@Valid MenuRequest request);

    void updateMenu(Long mid, @Valid MenuRequest request);

    void deleteMenu(Long mid);

    void assignMenuToRole(Long rid, @Valid AssignMenuRequest request);
}
