package com.example.demo.core.application.dto.response;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomBusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class RolePermissionResponse {

        private String roleName;
        private List<MenuPermissionResponse> menus;

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @Setter
        @Builder
        public static class MenuPermissionResponse {
            private String code;
            private List<String> permissions;
        }


        public static List<RolePermissionResponse> fromObject(Object value) {
                if (value instanceof List<?> list) {
                        ObjectMapper mapper = new ObjectMapper();
                        return list.stream()
                                .map(item -> mapper.convertValue(item, RolePermissionResponse.class))
                                .toList();
                }

                throw new CustomBusinessException(
                        ErrorCode.CACHE_DESERIALIZATION_ERROR.getCode(),
                        ErrorCode.CACHE_DESERIALIZATION_ERROR.getMessage()
                );
        }
}
