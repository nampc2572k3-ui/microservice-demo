package com.example.demo.common.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // role
    ROLE_NOT_FOUND(404, "Role Not Found"),
    ROLE_UNASSIGNED(400, "Role Unassigned"),

    // menu
    MENU_CODE_ALREADY_EXIST(409, "Menu Code Already Exist"),
    MENU_NOT_FOUND(404, "Menu Not Found"),
    MENU_ALREADY_ASSIGNED(400, "Menu Already Assigned"),

    // resource
    RESOURCE_ALREADY_EXISTS(409, "Resource Already Exists"),
    RESOURCE_NOT_FOUND(404, "Resource Not Found"),

    // permission
    UNAUTHORIZED_INTERNAL_CALL(401, "Unauthorized Internal Call"),


    // cache
    CACHE_DESERIALIZATION_ERROR(500, "Cannot convert cache value to object"),

    INTERNAL_SEVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String message;

}
