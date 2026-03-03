package com.example.demo.auth.service.impl;

import com.example.demo.auth.model.dto.request.RegisterRequest;
import com.example.demo.auth.model.entity.Account;
import com.example.demo.auth.model.entity.Role;
import com.example.demo.auth.repository.AccountRepository;
import com.example.demo.auth.repository.RoleRepository;
import com.example.demo.auth.service.AccountService;
import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomBusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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
                .enabled(false)
                .locked(false)
                .roles(roles)
                .build();

        // save account
        accountRepository.save(newAccount);

    }
}
