package com.example.demo.domain.model.dto.response;

import com.example.demo.domain.model.dto.projection.MenuResourceFlatProjection;
import lombok.*;

@Getter
@Setter
@Builder
public class ResourceResponse {
    private Long id;
    private String pathPattern;
    private String httpMethod;
    private String description;
    private String action;
    private boolean active;

    public static ResourceResponse from(MenuResourceFlatProjection r) {
        return ResourceResponse.builder()
                .id(r.getResourceId())
                .pathPattern(r.getPathPattern())
                .httpMethod(r.getHttpMethod())
                .description(r.getDescription())
                .action(r.getAction())
                .active(r.getResourceActive())
                .build();
    }
}
