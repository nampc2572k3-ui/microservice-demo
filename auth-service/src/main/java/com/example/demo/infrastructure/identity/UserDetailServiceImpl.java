package com.example.demo.infrastructure.identity;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.core.domain.model.entity.Account;
import com.example.demo.core.persistence.AccountRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(@NonNull String indentity) throws UsernameNotFoundException {
        Account user = (Account) accountRepository.findByUsernameOrEmail(indentity)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));
        return new UserDetailsImpl(user);
    }
}
