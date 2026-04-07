package com.example.demo.infrastructure.identity;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.core.adapter.http.PermissionClient;
import com.example.demo.core.domain.model.entity.Account;
import com.example.demo.core.persistence.AccountRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    private final PermissionClient permissionClient;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(@NonNull String indentity) throws UsernameNotFoundException {
        Account user = (Account) accountRepository.findByUsernameOrEmail(indentity)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));

        var roles = permissionClient.getPermissionsSafe(user.getId());

        Set<SimpleGrantedAuthority> authorities = roles.stream()
                .filter(role -> role.getMenus() != null)
                .flatMap(role -> role.getMenus().stream())
                .filter(menu -> menu.getPermissions() != null)
                .flatMap(menu -> menu.getPermissions().stream()
                        // MENU-PERMISSION
                        .map(permission -> menu.getCode() + "-" + permission)
                )
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return UserDetailsImpl.builder()
                .account(user)
                .authorities(authorities)
                .roles(roles)
                .build();
    }
}
