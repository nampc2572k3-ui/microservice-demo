package com.example.demo.core.domain.helper;

import com.example.demo.core.application.dto.projection.MenuFlatProjection;
import com.example.demo.core.application.dto.response.MenuTreeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenuHelper {

    public List<MenuTreeResponse> buildMenuTree(List<MenuFlatProjection> daos) {

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

        log.info("Converted flat menu list to map: {}", map);

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
