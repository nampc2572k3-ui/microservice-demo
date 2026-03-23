package com.example.demo.core.application.dto.response;

import com.example.demo.core.application.dto.projection.MenuOnlyProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class MenuResponse {
    private Long id;
    private String code;
    private String name;
    private Integer sortOrder;
    private boolean active;
    private Long parentId;
    private List<ResourceResponse> resources;


    public static MenuResponse from(
            MenuOnlyProjection m,
            Map<Long, List<ResourceResponse>> resourceMap
    ) {

        MenuResponse menu = MenuResponse.builder()
                .id(m.getMenuId())
                .code(m.getMenuCode())
                .name(m.getMenuName())
                .sortOrder(m.getSortOrder())
                .active(m.getActive())
                .parentId(m.getParentId())
                .build();

        menu.setResources(
                resourceMap.getOrDefault(m.getMenuId(), new ArrayList<>())
        );

        return menu;
    }

}
