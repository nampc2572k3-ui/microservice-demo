package com.example.demo.core.config.security;

import com.example.demo.auth.model.entity.Menu;
import com.example.demo.auth.model.entity.Resource;
import com.example.demo.auth.repository.AccountRepository;
import com.example.demo.auth.repository.ResourceRepository;
import com.example.demo.common.constant.ActionType;
import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;;
    private final ResourceRepository resourceRepository;
    private final AccountRepository accountRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final var authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtUtils.extractUsername(token);

            String method = request.getMethod();
            String uri = request.getRequestURI();

            Resource resource = resourceRepository
                    .findByHttpMethodAndPattern(method, uri);

            // If no resource mapping found, allow the filter chain to continue (or decide to block).
            if (resource == null) {
                log.debug("No resource mapping found for {} {}", method, uri);
                filterChain.doFilter(request, response);
                return;
            }

            Menu menu = resource.getMenu();
            ActionType action = resource.getAction();

            boolean allowed = accountRepository.existsByUsernameAndMenuIdAndPermission(
                    username, menu.getId(), action.getValue()
            );

            if (!allowed) {
                // Deny access
                response.setStatus(ErrorCode.USER_NOT_HAVE_PERMISSION.getCode());
                response.getWriter().write(ErrorCode.USER_NOT_HAVE_PERMISSION.getMessage());
                return;
            }

        } catch (Exception ex) {
            log.error("Error while validating JWT permissions", ex);
            // On unexpected error, fail closed: return 401
            response.setStatus(ErrorCode.SC_UNAUTHORIZED.getCode());
            response.getWriter().write(ErrorCode.SC_UNAUTHORIZED.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
