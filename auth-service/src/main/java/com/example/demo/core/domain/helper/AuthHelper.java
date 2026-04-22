package com.example.demo.core.domain.helper;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomBusinessException;
import com.example.demo.core.application.cache.BlacklistTokenCache;
import com.example.demo.core.application.cache.SessionCache;
import com.example.demo.core.application.dto.request.LoginRequest;
import com.example.demo.core.application.dto.response.common.TokenPairResponse;
import com.example.demo.core.domain.model.entity.Account;
import com.example.demo.core.domain.model.entity.AccountDevice;
import com.example.demo.core.domain.model.entity.LoginAttempt;
import com.example.demo.core.domain.model.enums.Platform;
import com.example.demo.core.persistence.AccountDeviceRepository;
import com.example.demo.core.persistence.LoginAttemptRepository;
import com.example.demo.infrastructure.context.ClientInfoContext;
import com.example.demo.infrastructure.identity.UserDetailsImpl;
import com.example.demo.infrastructure.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthHelper {

    private final AccountDeviceRepository accountDeviceRepository;
    private final LoginAttemptRepository loginAttemptRepository;

    private final SessionCache sessionCache;
    private final BlacklistTokenCache blacklistTokenCache;

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional(rollbackFor = Exception.class)
    public AccountDevice handleAccountDevice(LoginRequest request, Account acc) {

        Optional<AccountDevice> optionalDevice =
                accountDeviceRepository.findByIdAndAccountId(
                        request.getDeviceId(),
                        acc.getId()
                );

        AccountDevice device;

        if (optionalDevice.isPresent()) {

            device = optionalDevice.get();
            device.setDeviceName(request.getDeviceName());
            device.setPlatform(Platform.valueOf(request.getPlatform()));
            device.setPushToken(request.getPushToken());
            device.setLastActiveAt(LocalDateTime.now());

        } else {
            device = AccountDevice.builder()
                    .id(request.getDeviceId())
                    .deviceName(request.getDeviceName())
                    .platform(Platform.valueOf(request.getPlatform()))
                    .pushToken(request.getPushToken())
                    .trusted(false)
                    .lastActiveAt(LocalDateTime.now())
                    .account(acc)
                    .build();
        }

        accountDeviceRepository.save(device);

        return device;
    }

    @Transactional(rollbackFor = Exception.class)
    public void recordLoginAttempt(String email, String clientIp, boolean successful, String reason) {
        LoginAttempt attempt = LoginAttempt.builder()
                .email(email)
                .ipAddress(clientIp)
                .successful(successful)
                .failureReason(reason)
                .attemptedAt(LocalDateTime.now())
                .build();
        loginAttemptRepository.save(attempt);
    }

    public void validateSession(ClientInfoContext clientInfo, String jti, String accId, String refreshToken) {
        ClientInfoContext cachedInfo = sessionCache.get(jti, accId)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.SESSION_INVALID));

        if (blacklistTokenCache.isBlacklisted(refreshToken)) {
            throw new CustomBusinessException(ErrorCode.SESSION_INVALID);
        }

        if(cachedInfo.deviceId().equals(clientInfo.deviceId()) ){
            blacklistTokenCache.put(refreshToken, jwtProvider.getTokenExpirationRemaining(refreshToken));
            throw new CustomBusinessException(
                    ErrorCode.SESSION_INVALID_CLIENT_IP
            );
        }

        if(cachedInfo.userAgent().equals(clientInfo.userAgent()) ||
                clientInfo.userAgent() == null
        ){
            blacklistTokenCache.put(refreshToken, jwtProvider.getTokenExpirationRemaining(refreshToken));
            throw new CustomBusinessException(
                    ErrorCode.BROWSER_MISMATCH
            );
        }


        if (!cachedInfo.clientIp().equals(clientInfo.clientIp())) {
            log.info("Detected IP change for JTI {}: {} -> {}",
                    jti, cachedInfo.clientIp(), clientInfo.clientIp());
        }
    }

    public UserDetails authenticate(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getIdentity(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return (UserDetails) authentication.getPrincipal();
    }

    public TokenPairResponse buildToken(UserDetails userDetails) {
        String accessToken = jwtProvider.generateAccessToken(userDetails);
        String refreshToken = jwtProvider.generateRefreshToken(userDetails);

        return TokenPairResponse.from(accessToken, refreshToken);
    }

}
