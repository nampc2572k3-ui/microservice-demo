package com.example.demo.domain.model.dto.projection;

public interface MenuOnlyProjection {
    Long getMenuId();
    String getMenuCode();
    String getMenuName();
    Integer getSortOrder();
    Boolean getActive();
    Long getParentId();
}
