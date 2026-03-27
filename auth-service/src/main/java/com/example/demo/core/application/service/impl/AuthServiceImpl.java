package com.example.demo.core.application.service.impl;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomBusinessException;
import com.example.demo.core.adapter.http.PermissionClient;
import com.example.demo.core.application.dto.request.LoginRequest;
import com.example.demo.core.application.dto.request.RegisterRequest;
import com.example.demo.core.application.dto.response.LoginResponse;
import com.example.demo.core.application.service.AuthService;
import com.example.demo.core.domain.model.entity.Account;
import com.example.demo.core.persistence.AccountRepository;
import com.example.demo.infrastructure.identity.UserDetailsImpl;
import com.example.demo.infrastructure.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;

    private final PermissionClient permissionClient;

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

    @Transactional(readOnly = true)
    @Override
    public LoginResponse login(LoginRequest request) {

        // rate limit by Ip (TODO)

        // Authenticate & Context setup
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getIdentity(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtProvider.generateAccessToken(userDetails);
        String refreshToken = jwtProvider.generateRefreshToken(userDetails);

        UserDetailsImpl customUser = (UserDetailsImpl) userDetails;

        Account acc = customUser.account();



        // cache refresh token in redis (TODO)

        // build roles response (TODO)
        List<LoginResponse.RolePermissionResponse> roles = permissionClient.getPermissionsSafe(acc.getId());


        return LoginResponse.builder()
                .accountId(acc.getId())
                .username(acc.getUsername())
                .email(acc.getEmail())
                .roles(roles)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


}
