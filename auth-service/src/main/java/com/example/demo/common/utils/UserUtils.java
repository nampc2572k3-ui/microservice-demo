package com.example.demo.common.utils;

import com.example.demo.core.config.security.UserDetailsImpl;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

@UtilityClass
public class UserUtils {

    public static UserDetailsImpl getCurrentUser() {
        if("anonymousUser".equals(
                Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal()
        )) {
            return UserDetailsImpl.build(null, "SYSTEM", null, null, null, null);
        }

        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


}
