package com.example.demo.core.application.dto.projection;


public interface ResourcePermissionProjection {

    String getPathPattern();

    String getHttpMethod();

    Integer getBitmask();

}
