package com.example.demo.core.config.security;


import com.example.demo.auth.model.entity.Resource;
import com.example.demo.auth.service.cache.ResourceCacheService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class CustomAuthorizationManager
        implements AuthorizationManager<RequestAuthorizationContext> {

    private final ResourceCacheService resourceCacheService;

    @Override
    public AuthorizationDecision authorize(
            Supplier<? extends org.springframework.security.core.Authentication> authentication,
            RequestAuthorizationContext context) {

        Authentication auth = authentication.get();
        assert context != null;
        HttpServletRequest request = context.getRequest();

        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        Resource resource = resourceCacheService.findMatch(
                request.getMethod(),
                request.getRequestURI()
        );

        if (resource == null) {
            return new AuthorizationDecision(false);
        }

        String requiredAuthority =
                "MENU_" + resource.getMenu().getId() +
                        "_" + resource.getAction().getValue();

        boolean granted = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(requiredAuthority::equals);

        return new AuthorizationDecision(granted);
    }
}



