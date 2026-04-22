package com.example.demo.core.application.service.impl;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomBusinessException;
import com.example.demo.common.utils.ClientInfoUtils;
import com.example.demo.core.application.cache.BlacklistTokenCache;
import com.example.demo.core.application.cache.SessionCache;
import com.example.demo.core.application.dto.request.LoginRequest;
import com.example.demo.core.application.dto.request.RefreshTokenRequest;
import com.example.demo.core.application.dto.request.RegisterRequest;
import com.example.demo.core.application.dto.response.LoginResponse;
import com.example.demo.core.application.dto.response.RefreshTokenResponse;
import com.example.demo.core.application.service.AuthService;
import com.example.demo.core.domain.event.internal.LoginSuccessTransactionalEvent;
import com.example.demo.core.domain.event.internal.RegisterSuccessTransactionalEvent;
import com.example.demo.core.domain.helper.AuthHelper;
import com.example.demo.core.domain.model.entity.Account;
import com.example.demo.core.domain.model.entity.AccountDevice;
import com.example.demo.core.persistence.AccountRepository;
import com.example.demo.infrastructure.identity.UserDetailsImpl;
import com.example.demo.infrastructure.jwt.JwtProvider;
import com.example.demo.infrastructure.context.ClientInfoContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    private final AuthHelper authHelper;

    private final ApplicationEventPublisher eventPublisher;

    private final SessionCache sessionCache;
    private final BlacklistTokenCache blacklistTokenCache;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterRequest request) {

        if (accountRepository.existsAccountByUsername(request.getUsername())) {
            throw new CustomBusinessException(
                    ErrorCode.USERNAME_ALREADY_EXISTS
            );
        }

        if (accountRepository.existsAccountByEmail(request.getEmail())) {
            throw new CustomBusinessException(
                    ErrorCode.EMAIL_ALREADY_EXISTS
            );
        }

        Account account = Account.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                // must verify email before enable account
                .enabled(false)
                .locked(false)
                .build();

        accountRepository.save(account);

        //  publish event to send email
        eventPublisher.publishEvent(new RegisterSuccessTransactionalEvent(this, account));

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {

        ClientInfoContext clientInfo = ClientInfoUtils.extract(httpRequest);

        try {
            UserDetails userDetails = authHelper.authenticate(request);
            UserDetailsImpl customUser = (UserDetailsImpl) userDetails;
            Account acc = Objects.requireNonNull(customUser).getAccount();

            var tokenPair = authHelper.buildToken(userDetails);

            AccountDevice device = authHelper.handleAccountDevice(request, acc);

            sessionCache.put(
                    jwtProvider.parseJti(tokenPair.getRefreshToken()),
                    acc.getId(), device.getId(), clientInfo.userAgent(),
                    clientInfo.clientIp());

            // publish event login
            eventPublisher.publishEvent(new LoginSuccessTransactionalEvent(
                            this, acc, tokenPair.getRefreshToken(), device,  clientInfo.clientIp(), httpRequest
                    ));

            return LoginResponse.from(acc, tokenPair, customUser.getRoles());

        } catch (Exception e) {
            authHelper.recordLoginAttempt(request.getIdentity(), clientInfo.clientIp(), false, e.getMessage());

            log.error(e.getMessage());

            throw new CustomBusinessException(
                    ErrorCode.BAD_CREDENTIALS.getCode(),
                    e.getMessage()
            );
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest) {

        String refreshToken = request.getRefreshToken();
        ClientInfoContext clientInfo = ClientInfoUtils.extract(httpRequest);
        String accId = jwtProvider.extractAccountId(refreshToken);
        String jti = jwtProvider.parseJti(refreshToken);

        authHelper.validateSession(clientInfo, accId, jti, refreshToken);

        blacklistTokenCache.put(refreshToken, jwtProvider.getTokenExpirationRemaining(refreshToken));

        Account acc = accountRepository.findById(accId)
                .orElseThrow(() -> new CustomBusinessException(
                        ErrorCode.USER_NOT_FOUND));

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .account(acc)
                .build();

        var newTokenPair = authHelper.buildToken(userDetails);

        sessionCache.put(
                jwtProvider.parseJti(newTokenPair.getRefreshToken()), acc.getId(),
                clientInfo.deviceId(), clientInfo.userAgent(),
                clientInfo.clientIp()
        );

        return RefreshTokenResponse.builder()
                .tokenPair(newTokenPair)
                .username(acc.getUsername())
                .email(acc.getEmail())
                .accessExpiresIn(accessTokenExpirationMs)
                .refreshExpiresIn(refreshTokenExpirationMs)
                .build();
    }


}
