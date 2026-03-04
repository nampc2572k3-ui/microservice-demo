package com.example.demo.auth.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class AccountDetailResponse {

    private UUID accountId;
    private String username;
    private String email;
    private List<RoleResponse> roles;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class RoleResponse {
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
