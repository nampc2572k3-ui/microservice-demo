package com.example.demo.auth.service.impl;

import com.example.demo.auth.model.dto.request.LoginRequest;
import com.example.demo.auth.model.dto.request.RegisterRequest;
import com.example.demo.auth.model.dto.response.AccountDetailResponse;
import com.example.demo.auth.model.entity.Account;
import com.example.demo.auth.model.entity.Role;
import com.example.demo.auth.repository.AccountRepository;
import com.example.demo.auth.repository.RoleRepository;
import com.example.demo.auth.service.AccountService;
import com.example.demo.auth.service.cache.TokenRedisService;
import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomBusinessException;
import com.example.demo.common.utils.JwtUtils;
import com.example.demo.common.utils.UserUtils;
import com.example.demo.core.config.security.UserDetailsImpl;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final TokenRedisService tokenRedisService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void registerAccount(RegisterRequest request) {
        // check username email exist
        if (accountRepository.existsAccountByUsername(request.getUserName())) {
            throw new CustomBusinessException(
                    ErrorCode.USER_ALREADY_EXISTS.getCode(),
                    ErrorCode.USER_ALREADY_EXISTS.getMessage()
            );
        }

        if (accountRepository.existsAccountByEmail(request.getEmail())) {
            throw new CustomBusinessException(
                    ErrorCode.EMAIL_ALREADY_EXISTS.getCode(),
                    ErrorCode.EMAIL_ALREADY_EXISTS.getMessage()
            );
        }

        // find role by request.getRoleIds
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoleIds()));
        if (roles.size() != request.getRoleIds().size()) {
            log.error("Some roleIds not found: {}", request.getRoleIds());
            throw new CustomBusinessException(
                    ErrorCode.ROLE_NOT_FOUND.getCode(),
                    ErrorCode.ROLE_NOT_FOUND.getMessage()
            );
        }

        Account newAccount = Account.builder()
                .email(request.getEmail())
                .username(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .locked(false)
                .roles(roles)
                .build();

        // save account
        accountRepository.save(newAccount);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountDetailResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getInput(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        UserDetailsImpl customUser = (UserDetailsImpl) userDetails;
        Account acc = customUser.account();
        // cache refresh token in redis with expiration time same as refresh token
        tokenRedisService.storeRefreshToken(
                acc.getUsername(), refreshToken, jwtUtils.getRefreshTokenExpirationMs()
        );

        List<com.example.demo.auth.model.dto.response.AccountDetailResponse.RoleResponse> roles =
                acc.getRoles().stream()
                        .map(role -> com.example.demo.auth.model.dto.response.AccountDetailResponse.RoleResponse.builder()
                                .roleName(role.getName())
                                .menus(role.getRoleMenus().stream()
                                        .map(menu -> AccountDetailResponse.RoleResponse.MenuPermissionResponse.builder()
                                                .code(menu.getMenu().getCode())
                                                .permissions(UserUtils.extractPermissions(menu.getPermission()))
                                                .build())
                                        .toList())
                                .build())
                        .toList();

        //log roles
        log.info("User {} has roles: {}", acc.getUsername(), roles.stream().map(AccountDetailResponse.RoleResponse::getRoleName).toList());


        return AccountDetailResponse.builder()
                .accountId(acc.getId())
                .username(acc.getUsername())
                .email(acc.getEmail())
                .roles(roles)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
