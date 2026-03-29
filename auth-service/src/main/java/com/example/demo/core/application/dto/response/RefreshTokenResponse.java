package com.example.demo.core.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RefreshTokenResponse {

    private String accessToken;

    private String refreshToken;

    private long accessExpiresIn;

    private long refreshExpiresIn;

    private String accountId;

    private String username;

    private String email;

}
