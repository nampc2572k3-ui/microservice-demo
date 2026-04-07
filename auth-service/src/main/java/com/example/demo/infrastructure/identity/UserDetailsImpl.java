package com.example.demo.infrastructure.identity;

import com.example.demo.core.application.dto.response.LoginResponse;
import com.example.demo.core.domain.model.entity.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
public class UserDetailsImpl implements UserDetails {

    private Account account;

    private Collection<? extends GrantedAuthority> authorities;

    private List<LoginResponse.RolePermissionResponse> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
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
        return Objects.equals(account.getId(), user.account.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(account.getId());
    }

}
