package com.example.demo.core.config.security;

import com.example.demo.auth.service.cache.TokenRedisService;
import com.example.demo.common.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private final TokenRedisService redisBlacklistService;

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

        if (redisBlacklistService.isBlacklisted(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token has been revoked");
            return;
        }

        try {

            String username = jwtUtils.extractUsername(token);

            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                Set<String> authoritiesFromToken =
                        jwtUtils.extractAuthorities(token);

                List<SimpleGrantedAuthority> grantedAuthorities =
                        authoritiesFromToken.stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList();

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                grantedAuthorities
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);
            }

        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: " + ex.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
