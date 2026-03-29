package com.example.demo.common.utils;

import com.example.demo.core.domain.model.entity.Account;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class UserInfoUtils {

    public Account getCurrentAccount(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof Account) {
            return (Account) auth.getPrincipal();
        }
        return null;
    }
}
