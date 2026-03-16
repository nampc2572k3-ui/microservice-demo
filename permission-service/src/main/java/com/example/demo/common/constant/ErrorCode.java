package com.example.demo.common.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // role
    ROLE_NOT_FOUND(404, "Role Not Found"),
    ROLE_UNASSIGNED(400, "Role Unassigned"),


    INTERNAL_SEVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String message;

}
