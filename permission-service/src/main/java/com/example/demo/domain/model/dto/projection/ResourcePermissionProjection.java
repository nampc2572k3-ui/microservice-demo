package com.example.demo.domain.model.dto.projection;


public interface ResourcePermissionProjection {

    String getPathPattern();
    String getHttpMethod();
    Integer getBitmask();

}
