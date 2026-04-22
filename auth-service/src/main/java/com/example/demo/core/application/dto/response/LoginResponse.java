package com.example.demo.core.application.dto.response;

import com.example.demo.core.application.dto.response.common.TokenPairResponse;
import com.example.demo.core.domain.model.entity.Account;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
public class LoginResponse {

    private String accountId;

    private String username;

    private String email;

    private List<RolePermissionResponse> roles;

    private TokenPairResponse tokenPair;

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


    public static LoginResponse from(
            Account account,
            TokenPairResponse tokenPair,
            List<RolePermissionResponse> roles
    ) {
        return LoginResponse.builder()
                .accountId(account.getId())
                .username(account.getUsername())
                .email(account.getEmail())
                .roles(roles)
                .tokenPair(tokenPair)
                .build();
    }

}
