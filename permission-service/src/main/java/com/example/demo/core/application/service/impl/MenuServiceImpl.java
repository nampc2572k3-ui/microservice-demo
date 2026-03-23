package com.example.demo.core.application.service.impl;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomBusinessException;
import com.example.demo.core.domain.helper.MenuHelper;
import com.example.demo.core.application.dto.projection.MenuOnlyProjection;
import com.example.demo.core.application.dto.projection.MenuResourceFlatProjection;
import com.example.demo.core.application.dto.request.AssignMenuRequest;
import com.example.demo.core.application.dto.request.MenuRequest;
import com.example.demo.core.application.dto.request.common.PageRequest;
import com.example.demo.core.application.dto.response.MenuResponse;
import com.example.demo.core.application.dto.response.MenuTreeResponse;
import com.example.demo.core.application.dto.response.ResourceResponse;
import com.example.demo.core.domain.model.entity.Menu;
import com.example.demo.core.domain.model.entity.Resource;
import com.example.demo.core.domain.repository.MenuRepository;
import com.example.demo.core.domain.repository.ResourceRepository;
import com.example.demo.core.domain.repository.RoleMenuRepository;
import com.example.demo.core.application.service.CacheService;
import com.example.demo.core.application.service.MenuService;
import com.example.demo.core.application.service.cache.MenuCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final ResourceRepository resourceRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final MenuCacheService menuCacheService;
    private final CacheService cacheService;

    @Transactional(readOnly = true)
    @Override
    public List<MenuTreeResponse> getMenuTreeByAccount(String accId) {

        Optional<List<MenuTreeResponse>> cached = menuCacheService.get(accId);
        if (cached.isPresent()) return cached.get();

        List<MenuTreeResponse> tree = MenuHelper.buildMenuTree(
                menuRepository.findMenuByAccount(accId)
        );

        String version = cacheService.getOrInitVersion(accId);
        menuCacheService.put(accId, version, tree);

        return tree;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MenuResponse> getMenus(PageRequest request) {

        Pageable pageable = PageRequest.toPageable(
                request.getPage(),
                request.getSize(),
                request.getSortBy(),
                request.getDirection()
        );

        Page<MenuOnlyProjection> menuPage = menuRepository.findMenus(pageable);

        log.info("MenuOnlyProjection page: {}", menuPage);

        List<Long> menuIds = menuPage.getContent()
                .stream()
                .map(MenuOnlyProjection::getMenuId)
                .toList();

        if (menuIds.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, menuPage.getTotalElements());
        }

        // get all resources daos
        List<MenuResourceFlatProjection> resourceDaos =
                resourceRepository.findResourcesByMenuIds(menuIds);

        // map menuId to list of ResourceResponse
        Map<Long, List<ResourceResponse>> resourceMap = resourceDaos.stream()
                .filter(r -> r.getResourceId() != null)
                .collect(Collectors.groupingBy(
                        MenuResourceFlatProjection::getMenuId,
                        Collectors.mapping(ResourceResponse::from, Collectors.toList())
                ));

        log.info("Resource map for menuIds {}: {}", menuIds, resourceMap);

        // build response
        List<MenuResponse> content = menuPage.getContent()
                .stream()
                .map(m -> MenuResponse.from(m, resourceMap))
                .toList();

        return new PageImpl<>(content, pageable, menuPage.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createMenu(MenuRequest request) {

        if (menuRepository.findByCodeAndDeletedIsFalse(request.getCode()).isPresent()) {
            throw new CustomBusinessException(
                    ErrorCode.MENU_CODE_ALREADY_EXIST.getCode(),
                    ErrorCode.MENU_CODE_ALREADY_EXIST.getMessage()
            );
        }

        Menu menu = Menu.builder()
                .code(request.getCode())
                .name(request.getName())
                .sortOrder(request.getSortOrder())
                .active(request.isActive())
                .build();

        if (request.getParentId() != null) {
            Menu parent = menuRepository.findById(request.getParentId())
                    .orElseThrow(() -> new CustomBusinessException(
                            ErrorCode.MENU_NOT_FOUND.getCode(),
                            ErrorCode.MENU_NOT_FOUND.getMessage()
                    ));
            menu.setParent(parent);
        } else {
            menu.setParent(null);
        }

        if (!request.getResourceIds().isEmpty()) {
            Set<Resource> resources = new HashSet<>(
                    resourceRepository.findAllById(request.getResourceIds())
            );
            menu.setResources(resources);
        }

        menuRepository.save(menu);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateMenu(Long mid, MenuRequest request) {
        Menu menu = menuRepository.findById(mid)
                .orElseThrow(() -> new CustomBusinessException(
                        ErrorCode.MENU_NOT_FOUND.getCode(),
                        ErrorCode.MENU_NOT_FOUND.getMessage()
                ));

        menu.setCode(request.getCode());
        menu.setName(request.getName());
        menu.setSortOrder(request.getSortOrder());
        menu.setActive(request.isActive());

        if (request.getParentId() == null) {
            menu.setParent(null);
        } else {
            if (menu.getParent() == null || !menu.getParent().getId().equals(request.getParentId())) {

                Menu parent = menuRepository.findById(request.getParentId())
                        .orElseThrow(() -> new CustomBusinessException(
                                ErrorCode.MENU_NOT_FOUND.getCode(),
                                ErrorCode.MENU_NOT_FOUND.getMessage()
                        ));

                menu.setParent(parent);
            }
        }

        if (!request.getResourceIds().isEmpty()) {
            Set<Resource> resources = new HashSet<>(
                    resourceRepository.findAllById(request.getResourceIds())
            );
            menu.setResources(resources);
        }

        menuRepository.save(menu);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteMenu(Long mid) {
        Menu menu = menuRepository.findById(mid)
                .orElseThrow(() -> new CustomBusinessException(
                        ErrorCode.MENU_NOT_FOUND.getCode(),
                        ErrorCode.MENU_NOT_FOUND.getMessage()
                ));

        menu.setDeleted(true);
        menuRepository.save(menu);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void assignMenuToRole(Long rid, AssignMenuRequest request) {
        roleMenuRepository.upsertRoleMenu(
                rid,
                request.getMenuId(),
                request.getBitmask()
        );
    }


}
