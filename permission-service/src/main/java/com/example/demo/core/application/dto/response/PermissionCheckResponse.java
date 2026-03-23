package com.example.demo.core.application.dto.response;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomBusinessException;
import com.example.demo.core.domain.helper.PermissionHelper;
import com.example.demo.core.application.dto.projection.ResourcePermissionProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class PermissionCheckResponse {

    private boolean allowed;
    private String reason;


    public static PermissionCheckResponse fromObject(Object value) {
        if (value instanceof PermissionCheckResponse r) return r;

        if (value instanceof Map<?,?> map) {
            return PermissionCheckResponse.builder()
                    .allowed((Boolean) map.get("allowed"))
                    .reason((String) map.get("reason"))
                    .build();
        }

        throw new CustomBusinessException(
                ErrorCode.CACHE_DESERIALIZATION_ERROR.getCode(),
                ErrorCode.CACHE_DESERIALIZATION_ERROR.getMessage()
        );
    }

    public static PermissionCheckResponse from(ResourcePermissionProjection dao) {
        if (dao.getBitmask() == null || dao.getBitmask() == 0) {
            return PermissionCheckResponse.deny("NO_PERMISSION");
        } else if (PermissionHelper.hasPermission(dao.getBitmask(), dao.getHttpMethod())) {
           return PermissionCheckResponse.allow("OK");
        } else {
            return PermissionCheckResponse.deny("NO_PERMISSION");
        }
    }

    public static PermissionCheckResponse allow(String reason) {
        return PermissionCheckResponse.builder().allowed(true).reason(reason).build();
    }

    public static PermissionCheckResponse deny(String reason) {
        return PermissionCheckResponse.builder().allowed(false).reason(reason).build();
    }

}
