package com.example.demo.domain.model.dto.projection;

public interface MenuFlatProjection {
    Long getId();

    String getCode();

    String getName();

    Integer getSortOrder();

    Long getParentId();

    Integer getBitmask();
}
