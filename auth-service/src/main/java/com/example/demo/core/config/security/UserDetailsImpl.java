package com.example.demo.core.config.security;

import com.example.demo.auth.model.entity.Account;
import com.example.demo.auth.model.entity.Menu;
import com.example.demo.common.constant.ActionType;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
public record UserDetailsImpl(Account account) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> roleAuthorities =
                account.getRoles()
                        .stream()
                        .filter(Objects::nonNull)
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                        .collect(Collectors.toSet());

        Set<GrantedAuthority> resourceAuthorities =
                account.getRoles()
                        .stream()
                        .filter(Objects::nonNull)
                        .flatMap(role -> role.getRoleMenus().stream())
                        .filter(Objects::nonNull)
                        .flatMap(roleMenu -> {
                            int permissionMask = roleMenu.getPermission();
                            Menu menu = roleMenu.getMenu();
                            if (menu == null) {
                                return Stream.empty();
                            }
                            return Stream.of(ActionType.values())
                                    .filter(action -> (permissionMask & action.getValue()) == action.getValue())
                                    .map(action -> new SimpleGrantedAuthority(
                                            "MENU_" + menu.getId() + "_" + action.getValue()
                                    ));
                        })
                        .collect(Collectors.toSet());

        roleAuthorities.addAll(resourceAuthorities);
        return roleAuthorities;
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
