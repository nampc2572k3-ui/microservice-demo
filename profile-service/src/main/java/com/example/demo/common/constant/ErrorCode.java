package com.example.demo.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    USER_NOT_FOUND(1001, "User not found"),
    INTERNAL_SERVER_ERROR(500, "Internal server error");


    private final int code;
    private final String message;

}
