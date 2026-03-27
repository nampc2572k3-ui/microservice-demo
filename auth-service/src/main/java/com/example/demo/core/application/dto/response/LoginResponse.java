package com.example.demo.core.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
public class LoginResponse {

    private String accountId;

    private String username;

    private String password;

    private String email;

    private List<RolePermissionResponse> roles;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class RolePermissionResponse {
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


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String accessToken;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String refreshToken;


}
