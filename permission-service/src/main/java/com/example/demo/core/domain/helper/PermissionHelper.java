package com.example.demo.core.domain.helper;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomBusinessException;
import com.example.demo.core.application.dto.projection.PermissionProjection;
import com.example.demo.core.application.dto.projection.ResourcePermissionProjection;
import com.example.demo.core.application.dto.response.PermissionCheckResponse;
import com.example.demo.core.application.dto.response.RolePermissionResponse;
import com.example.demo.core.domain.model.enums.ActionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionHelper {

    public String normalizePath(String path) {
        return path.replaceAll("/[0-9a-fA-F\\-]{8,}", "/{id}")
                .replaceAll("/\\d+", "/{id}");
    }

    public boolean hasPermission(Integer mergedBitmask, String method) {
        ActionType action = mapMethodToAction(method);
        return (mergedBitmask & action.getValue()) != 0;
    }

    private ActionType mapMethodToAction(String method) {
        return switch (method.toUpperCase()) {
            case "GET" -> ActionType.READ;
            case "POST", "PUT", "PATCH" -> ActionType.MODIFY;
            case "DELETE" -> ActionType.DELETE;
            default -> throw new IllegalArgumentException("Unsupported method: " + method);
        };
    }

    public void validateInternalSecret(String secret) {
        String expected = System.getenv("INTERNAL_SECRET");
        if (expected != null && !expected.equals(secret)) {
            throw new CustomBusinessException(
                    ErrorCode.UNAUTHORIZED_INTERNAL_CALL.getCode(),
                    ErrorCode.UNAUTHORIZED_INTERNAL_CALL.getMessage()
            );
        }
    }

    private static List<String> mapBitmaskToPermissions(int bitmask) {
        return Arrays.stream(ActionType.values())
                .filter(action -> (bitmask & action.getValue()) != 0)
                .map(Enum::name)
                .toList();
    }

    public PermissionCheckResponse buiPermissionCheckResponse(ResourcePermissionProjection dao){
        if (dao.getBitmask() == null || dao.getBitmask() == 0) {
            return PermissionCheckResponse.deny("NO_PERMISSION");
        } else if (hasPermission(dao.getBitmask(), dao.getHttpMethod())) {
            return PermissionCheckResponse.allow("OK");
        } else {
            return PermissionCheckResponse.deny("NO_PERMISSION");
        }
    }


    public List<RolePermissionResponse> buildRolePermission(List<PermissionProjection> daos) {
        // Map: roleName -> (menuCode -> permissions)
        Map<String, Map<String, Set<String>>> roleMap = new HashMap<>();

        for (PermissionProjection row : daos) {
            roleMap
                    .computeIfAbsent(row.getRoleName(), r -> new HashMap<>())
                    .computeIfAbsent(row.getMenuCode(), m -> new HashSet<>())
                    .addAll(mapBitmaskToPermissions(row.getBitmask()));
        }

        // Convert sang DTO
        return roleMap.entrySet().stream()
                .map(roleEntry -> {

                    List<RolePermissionResponse.MenuPermissionResponse> menus =
                            roleEntry.getValue().entrySet().stream()
                                    .map(menuEntry ->
                                            RolePermissionResponse.MenuPermissionResponse.builder()
                                                    .code(menuEntry.getKey())
                                                    .permissions(new ArrayList<>(menuEntry.getValue()))
                                                    .build()
                                    )
                                    .toList();

                    return RolePermissionResponse.builder()
                            .roleName(roleEntry.getKey())
                            .menus(menus)
                            .build();
                })
                .toList();
    }

}
