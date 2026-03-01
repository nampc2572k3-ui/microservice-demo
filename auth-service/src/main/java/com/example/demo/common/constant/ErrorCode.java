package com.example.demo.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // ================= Auth =================
    AUTH_TOKEN_EXPIRED(401, "Token expired"),
    AUTH_INVALID_TOKEN(401, "Invalid token"),
    AUTH_TOKEN_SIGNATURE_INVALID(401, "Token signature invalid"),
    USER_NOT_FOUND(404, "User not found"),
    USER_NOT_HAVE_PERMISSION(403, "User does not have permission to access this resource"),
    SC_UNAUTHORIZED(401, "Unauthorized");


    private final int code;
    private final String message;

}
