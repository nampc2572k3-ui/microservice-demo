package com.example.demo.domain.helper;

import com.example.demo.domain.model.dto.projection.MenuFlatProjection;
import com.example.demo.domain.model.dto.response.MenuTreeResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class MenuHelper {

    public static List<MenuTreeResponse> buildMenuTree(List<MenuFlatProjection> daos) {

        Map<Long, MenuTreeResponse> map = daos.stream()
                .map(d -> MenuTreeResponse.builder()
                        .id(d.getId())
                        .code(d.getCode())
                        .name(d.getName())
                        .sortOrder(d.getSortOrder())
                        .parentId(d.getParentId())
                        .bitmask(d.getBitmask())
                        .build())
                .collect(Collectors.toMap(MenuTreeResponse::getId, Function.identity()));

        List<MenuTreeResponse> roots = new ArrayList<>();

        for (MenuTreeResponse node : map.values()) {

            if (node.getParentId() == null) {
                roots.add(node);
                continue;
            }

            MenuTreeResponse parent = map.get(node.getParentId());

            if (parent != null) {
                parent.getChildren().add(node);
            }
        }

        // log tree structure for debugging
        log.debug("Built menu tree: {}", roots);

        return roots;
    }

}
