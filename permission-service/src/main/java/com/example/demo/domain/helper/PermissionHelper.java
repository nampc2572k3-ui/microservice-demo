package com.example.demo.domain.helper;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomBusinessException;
import com.example.demo.domain.model.enums.ActionType;

public class PermissionHelper {

    public static String normalizePath(String path) {
        return path.replaceAll("/[0-9a-fA-F\\-]{8,}", "/{id}")
                .replaceAll("/\\d+", "/{id}");
    }

    public static boolean hasPermission(Integer mergedBitmask, String method) {
        ActionType action = mapMethodToAction(method);
        return (mergedBitmask & action.getValue()) != 0;
    }

    private static ActionType mapMethodToAction(String method) {
        return switch (method.toUpperCase()) {
            case "GET" -> ActionType.READ;
            case "POST", "PUT", "PATCH" -> ActionType.MODIFY;
            case "DELETE" -> ActionType.DELETE;
            default -> throw new IllegalArgumentException("Unsupported method: " + method);
        };
    }

    public static void validateInternalSecret(String secret) {
        String expected = System.getenv("INTERNAL_SECRET");
        if (expected != null && !expected.equals(secret)) {
            throw new CustomBusinessException(
                    ErrorCode.UNAUTHORIZED_INTERNAL_CALL.getCode(),
                    ErrorCode.UNAUTHORIZED_INTERNAL_CALL.getMessage()
            );
        }
    }

}
