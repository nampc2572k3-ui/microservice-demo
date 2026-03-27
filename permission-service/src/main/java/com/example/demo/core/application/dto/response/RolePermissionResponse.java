package com.example.demo.core.application.dto.response;

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

}
