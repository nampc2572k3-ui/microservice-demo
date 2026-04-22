package com.example.demo.core.application.dto.response;

import com.example.demo.core.application.dto.response.common.TokenPairResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RefreshTokenResponse {

    private long accessExpiresIn;

    private long refreshExpiresIn;

    private String accountId;

    private String username;

    private String email;

    private TokenPairResponse tokenPair;

}
