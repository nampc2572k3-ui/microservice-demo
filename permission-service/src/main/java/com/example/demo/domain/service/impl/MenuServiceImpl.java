package com.example.demo.domain.service.impl;

import com.example.demo.domain.helper.MenuHelper;
import com.example.demo.domain.model.dto.projection.MenuFlatProjection;
import com.example.demo.domain.model.dto.projection.MenuResourceFlatProjection;
import com.example.demo.domain.model.dto.response.MenuResponse;
import com.example.demo.domain.model.dto.response.MenuTreeResponse;
import com.example.demo.domain.model.dto.response.ResourceResponse;
import com.example.demo.domain.model.enums.ActionType;
import com.example.demo.domain.repository.MenuRepository;
import com.example.demo.domain.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final MenuHelper menuHelper;

    @Transactional(readOnly = true)
    @Override
    public List<MenuTreeResponse> getMenuTreeByAccount(String accId) {
        List<MenuFlatProjection> daos =
                menuRepository.findMenuByAccount(accId);

        log.debug("MenuFlatProjection for account {}: {}", accId, daos);

        return menuHelper.buildMenuTree(daos);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MenuResponse> getAll() {

        List<MenuResourceFlatProjection> daos = menuRepository.findAllMenuWithResources();

        Map<Long, MenuResponse> map = new LinkedHashMap<>();

        log.debug("MenuResourceFlatProjection: {}", daos);

        for (MenuResourceFlatProjection r : daos) {

            MenuResponse menu = map.computeIfAbsent(
                    r.getMenuId(),
                    id -> {
                        MenuResponse m = MenuResponse.builder()
                                .id(r.getMenuId())
                                .code(r.getMenuCode())
                                .name(r.getMenuName())
                                .sortOrder(r.getSortOrder())
                                .active(r.getActive())
                                .parentId(r.getParentId())
                                .build();

                        m.setResources(new ArrayList<>());
                        return m;
                    }
            );

            if (r.getResourceId() != null) {
                ResourceResponse res = ResourceResponse.builder()
                        .id(r.getResourceId())
                        .pathPattern(r.getPathPattern())
                        .httpMethod(r.getHttpMethod())
                        .description(r.getDescription())
                        .action(r.getAction())
                        .active(r.getResourceActive())
                        .build();

                menu.getResources().add(res);
            }
        }

        return new ArrayList<>(map.values());
    }


}
