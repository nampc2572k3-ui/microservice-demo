package com.example.demo.common.utils;

import com.example.demo.auth.model.entity.Role;
import com.example.demo.common.constant.ErrorCode;
import com.example.demo.core.config.security.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@Getter
@RequiredArgsConstructor
public class JwtUtils {

    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_ACCOUNT_ID = "accountId";
    private static final String CLAIM_ROLE = "role";

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(userDetails, accessTokenExpirationMs, "access");
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails, refreshTokenExpirationMs, "refresh");
    }

    private String buildToken(UserDetails userDetails, long expirationMillis, String type) {
        UserDetailsImpl customUser = (UserDetailsImpl) userDetails;

        Map<String, Object> claims = Map.of(
                CLAIM_TYPE, type,
                CLAIM_ACCOUNT_ID, customUser.account().getId(),
                CLAIM_ROLE, customUser.account().getRoles()
                                                    .stream()
                                                    .map(Role::getName)
                                                    .collect(Collectors.toSet())
        );

        return Jwts.builder()
                .subject(customUser.getUsername())
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSignKey())
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(parseToken(token).getPayload());
    }


    private Jws<Claims> parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token);

        } catch (ExpiredJwtException e) {
            throw new BadCredentialsException(
                    ErrorCode.AUTH_TOKEN_EXPIRED.getMessage(), e);
        } catch (MalformedJwtException e) {
            throw new BadCredentialsException(
                    ErrorCode.AUTH_INVALID_TOKEN.getMessage(), e);
        } catch (SignatureException e) {
            throw new BadCredentialsException(
                    ErrorCode.AUTH_TOKEN_SIGNATURE_INVALID.getMessage(), e);
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return userDetails.getUsername().equals(extractUsername(token)) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public UUID extractAccountId(String token) {
        var raw = extractClaim(token, claims -> claims.get(CLAIM_ACCOUNT_ID));
        return raw != null ? UUID.fromString(raw.toString()) : null;
    }


    public boolean isRefreshToken(String token) {
        return "refresh".equals(parseStringClaim(token));
    }

    private String parseStringClaim(String token) {
        var raw = extractClaim(token, claims -> claims.get(JwtUtils.CLAIM_TYPE));
        return Objects.toString(raw, null);
    }
}
