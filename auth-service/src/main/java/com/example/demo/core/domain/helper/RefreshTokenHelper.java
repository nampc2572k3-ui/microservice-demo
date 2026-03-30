package com.example.demo.core.domain.helper;

import com.example.demo.core.application.cache.TokenCache;
import com.example.demo.core.domain.model.entity.Account;
import com.example.demo.core.domain.model.entity.AccountDevice;
import com.example.demo.core.domain.model.entity.RefreshToken;
import com.example.demo.core.persistence.RefreshTokenRepository;
import com.example.demo.infrastructure.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RefreshTokenHelper {

    private final JwtProvider jwtProvider;

    private final RefreshTokenRepository refreshTokenRepository;

    private final TokenCache tokenCacheService;

    public void createRefreshToken(
            Account account, String refreshToken,
            AccountDevice device, String clientIp,
            HttpServletRequest request
    ) {

        // create a new refresh token record in the database

        String jti = jwtProvider.parseJti(refreshToken);

        RefreshToken token = RefreshToken.builder()
                .account(account)
                .jti(jti)
                .deviceId(device != null ? device.getId() : null)
                .userAgent(request.getHeader("User-Agent"))
                .ipAddress(clientIp)
                .expiresAt(jwtProvider.extractExpiration(refreshToken))
                .build();

        refreshTokenRepository.save(token);

        // create a new refresh token record in redis
        tokenCacheService.storeRefreshToken(
                account.getId(), refreshToken,
                jwtProvider.getTokenExpirationRemaining(refreshToken)
        );

    }
}
