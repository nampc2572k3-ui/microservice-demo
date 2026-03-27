package com.example.demo.infrastructure.identity;

import com.example.demo.core.domain.model.entity.Account;
import com.example.demo.core.domain.model.entity.AccountRole;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record UserDetailsImpl(Account account) implements UserDetails {

    public static UserDetailsImpl build(String userId,
                                        String userNm,
                                        String email,
                                        String userStatus,
                                        String password,
                                        List<String> roles) {

        return UserDetailsImpl.builder()
                .account(Account.builder()
                        .id(userId)
                        .email(email)
                        .username(userNm)
                        .password(password)
                        .enabled("ACTIVE".equals(userStatus))
                        .locked("LOCKED".equals(userStatus))
                        .build())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return account.getAccountRoles()
                .stream()
                .map(AccountRole::getRoleId)
                .map(roleId -> new SimpleGrantedAuthority("ROLE_" + roleId))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getUsername();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !account.isLocked();
    }

    @Override
    public boolean isEnabled() {
        return account.isEnabled();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(account.getId(), user.account().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(account.getId());
    }

}
