package com.example.demo.domain.service;

import com.example.demo.domain.model.dto.response.MenuResponse;
import com.example.demo.domain.model.dto.response.MenuTreeResponse;

import java.util.List;

public interface MenuService {

    List<MenuTreeResponse> getMenuTreeByAccount(String accId);

    List<MenuResponse> getAll();
}
