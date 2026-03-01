package com.example.demo.core.config.security;

import com.example.demo.auth.model.entity.Account;
import com.example.demo.auth.repository.AccountRepository;
import com.example.demo.common.constant.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(@NonNull String input) throws UsernameNotFoundException {
        Account user = (Account) accountRepository.findByUsernameOrEmail(input)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));
        return new UserDetailsImpl(user);

    }
}
