package com.example.demo.core.application.service.impl;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomBusinessException;
import com.example.demo.core.application.cache.SessionCache;
import com.example.demo.core.application.dto.request.LoginRequest;
import com.example.demo.core.application.dto.request.RefreshTokenRequest;
import com.example.demo.core.application.dto.request.RegisterRequest;
import com.example.demo.core.application.dto.response.LoginResponse;
import com.example.demo.core.application.dto.response.RefreshTokenResponse;
import com.example.demo.core.application.service.AuthService;
import com.example.demo.core.domain.event.internal.LoginSuccessTransactionalEvent;
import com.example.demo.core.domain.helper.AuthHelper;
import com.example.demo.core.domain.model.entity.Account;
import com.example.demo.core.domain.model.entity.AccountDevice;
import com.example.demo.core.persistence.AccountRepository;
import com.example.demo.infrastructure.identity.UserDetailsImpl;
import com.example.demo.infrastructure.jwt.JwtProvider;
import com.example.demo.infrastructure.metadata.SessionMetadata;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterRequest request) {

        // check username email exist
        if (accountRepository.existsAccountByUsername(request.getUsername())) {
            throw new CustomBusinessException(
                    ErrorCode.USERNAME_ALREADY_EXISTS.getCode(),
                    ErrorCode.USERNAME_ALREADY_EXISTS.getMessage()
            );
        }

        if (accountRepository.existsAccountByEmail(request.getEmail())) {
            throw new CustomBusinessException(
                    ErrorCode.EMAIL_ALREADY_EXISTS.getCode(),
                    ErrorCode.EMAIL_ALREADY_EXISTS.getMessage()
            );
        }

        // create account
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

        // send verification email (TODO) publish event to send email


    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String clientIp = authHelper.getClientIp(httpRequest);

        String userAgent = httpRequest.getHeader("User-Agent");

        try {
            // Authenticate & Context setup
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getIdentity(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserDetailsImpl customUser = (UserDetailsImpl) userDetails;
            Account acc = customUser.getAccount();

            // save device info
            AccountDevice device = authHelper.handleAccountDevice(request, acc);

            String accessToken = jwtProvider.generateAccessToken(userDetails);
            String refreshToken = jwtProvider.generateRefreshToken(userDetails);

            // store refresh token
            sessionCache.put(
                    jwtProvider.parseJti(refreshToken), acc.getId(),
                    device.getId(), userAgent,
                    clientIp
            );

            // publish login event
            eventPublisher.publishEvent(
                    new LoginSuccessTransactionalEvent(acc, refreshToken, device, clientIp, httpRequest, this)
            );

            return LoginResponse.builder()
                    .accountId(acc.getId())
                    .username(acc.getUsername())
                    .email(acc.getEmail())
                    .roles(customUser.getRoles())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (Exception e) {
            authHelper.recordLoginAttempt(request.getIdentity(), clientIp, false, e.getMessage());

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

        String clientIp = authHelper.getClientIp(httpRequest);

        String accId = jwtProvider.extractAccountId(refreshToken);
        String jti = jwtProvider.parseJti(refreshToken);

        String userAgent = httpRequest.getHeader("User-Agent");
        String deviceId = httpRequest.getHeader("Device-Id");


        SessionMetadata cachedMetadata = sessionCache.get(jti, accId)
                .orElseThrow( () -> new CustomBusinessException (
                    ErrorCode.SESSION_INVALID.getCode(),
                    ErrorCode.SESSION_INVALID.getMessage()
                ));

        if(cachedMetadata.deviceId() != deviceId){
            // put refresh token into blacklist todo
            throw new CustomBusinessException(
                    ErrorCode.SESSION_INVALID_CLIENT_IP.getCode(),
                    ErrorCode.SESSION_INVALID_CLIENT_IP.getMessage()
            );
        }

        if(cachedMetadata.userAgent() != userAgent || userAgent == null){
            // put refresh token into blacklist todo
            throw new CustomBusinessException(
                    ErrorCode.BROWSER_MISMATCH.getCode(),
                    ErrorCode.BROWSER_MISMATCH.getMessage()
            );
        }

        // Check 3: IP (Nếu IP thay đổi, có thể chỉ log lại hoặc yêu cầu xác thực thêm)
        if (!cachedMetadata.clientIp().equals(clientIp)) {
            log.info("IP thay đổi từ {} sang {} cho JTI: {}", cachedMetadata.clientIp(), clientIp, jti);
            // Với IP, chúng ta có thể nới lỏng hơn nếu DeviceID và UserAgent vẫn khớp.
        }

        // put refresh token into blacklist todo

        Account acc = accountRepository.findById(accId)
                .orElseThrow(() -> new CustomBusinessException(
                        ErrorCode.USER_NOT_FOUND.getCode(),
                        ErrorCode.USER_NOT_FOUND.getMessage()));


        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .account(acc)
                .build();

        String newAccessToken = jwtProvider.generateAccessToken(userDetails);
        String newRefreshToken = jwtProvider.generateRefreshToken(userDetails);

        // store refresh token
        sessionCache.put(
                jwtProvider.parseJti(newRefreshToken), acc.getId(),
                deviceId, userAgent,
                clientIp
        );

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .username(acc.getUsername())
                .email(acc.getEmail())
                .accessExpiresIn(accessTokenExpirationMs)
                .refreshExpiresIn(refreshTokenExpirationMs)
                .build();
    }


}
