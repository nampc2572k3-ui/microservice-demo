package com.example.demo.domain.model.dto.projection;

public interface MenuResourceFlatProjection {
    Long getMenuId();

    Long getResourceId();
    String getPathPattern();
    String getHttpMethod();
    String getDescription();
    String getAction();
    Boolean getResourceActive();


}
