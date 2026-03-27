package com.example.demo.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // authentication
    USER_NOT_FOUND(1001, "User Not Found"),
    SC_UNAUTHORIZED(1002, "Unauthorized"),

    // token
    AUTH_TOKEN_EXPIRED(2001, "Authentication Token Expired"),
    AUTH_INVALID_TOKEN(2002, "Authentication Token Invalid"),
    AUTH_TOKEN_SIGNATURE_INVALID(2003, "Authentication Token Signature Invalid"),
    TOKEN_HAS_BEEN_REVOKED(2004, "Token Has Been Revoked"),

    // register
    USERNAME_ALREADY_EXISTS(3001, "Username Already Exists"),
    EMAIL_ALREADY_EXISTS(3002, "Email Already Exists"),
    FAILED_TO_ASSIGN_ROLE(3003, "Failed to Assign Role"),


    INTERNAL_SEVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String message;
}
