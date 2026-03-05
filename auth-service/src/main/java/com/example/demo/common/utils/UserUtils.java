package com.example.demo.common.utils;

import com.example.demo.core.config.security.UserDetailsImpl;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class UserUtils {

    public static UserDetailsImpl getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return UserDetailsImpl.build(null, "SYSTEM", null, null, null, null);
        }

        Object principal = auth.getPrincipal();
        if (principal == null) {
            return UserDetailsImpl.build(null, "SYSTEM", null, null, null, null);
        }

        if ("anonymousUser".equals(principal)) {
            return UserDetailsImpl.build(null, "SYSTEM", null, null, null, null);
        }

        return (UserDetailsImpl) principal;
    }



}
