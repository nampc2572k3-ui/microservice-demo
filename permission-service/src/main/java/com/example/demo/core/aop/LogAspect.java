package com.example.demo.core.aop;

import com.example.demo.infrastructure.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final ObjectProvider<HttpServletRequest> requestProvider;

    private static final Pattern JWT_PATTERN =
            Pattern.compile("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$");


    private static final String POINTCUT =
            "execution(* com.example.demo..controller..*(..)) || " +
                    "execution(* com.example.demo..service..*(..))";

    @Around(POINTCUT)
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        long startNanos = System.nanoTime();

        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String username = getCurrentUsername();


        String httpMethod = "NO_HTTP";
        String requestUri = "NO_HTTP";
        try {
            HttpServletRequest currentRequest = requestProvider.getIfAvailable();
            if (currentRequest != null) {
                httpMethod = currentRequest.getMethod();
                requestUri = currentRequest.getRequestURI();
            }
        } catch (Exception ignored) {
        }

        try {

            log.info(
                    "[REQUEST] user={} {} {} -> {}.{} args={}",
                    username,
                    httpMethod,
                    requestUri,
                    className,
                    methodName,
                    sanitizeArgs(joinPoint.getArgs())
            );

            Object result = joinPoint.proceed();

            double durationMs = (System.nanoTime() - startNanos) / 1_000_000.0;
            String durationStr = String.format("%.3f", durationMs);

            log.info(
                    "[RESPONSE] user={} {}.{} executed in {} ms",
                    username,
                    className,
                    methodName,
                    durationStr
            );

            return result;

        } catch (Exception e) {

            double durationMs = (System.nanoTime() - startNanos) / 1_000_000.0;
            String durationStr = String.format("%.3f", durationMs);

            log.error(
                    "[ERROR] user={} {}.{} failed after {} ms",
                    username,
                    className,
                    methodName,
                    durationStr,
                    e
            );

            throw e;
        }
    }

    private String getCurrentUsername() {
        return UserContext.getUsername() != null ? UserContext.getUsername() : "ANONYMOUS";
    }

    private Object sanitizeArgs(Object[] args) {
        if (args == null) return null;

        return Arrays.stream(args)
                .map(arg -> {
                    try {
                        if (arg == null) return null;

                        // hide HttpServletRequest to avoid logging headers/bodies
                        if (arg instanceof HttpServletRequest) return "<HTTP_REQUEST>";

                        // hide binary content
                        if (arg instanceof byte[]) return "<BINARY>";

                        // Collections/arrays: map elements
                        if (arg instanceof Collection<?> coll) {
                            return coll.stream()
                                    .map(Object::toString)
                                    .map(this::redactIfSensitive)
                                    .toList();
                        }

                        // If it's a string, check for JWT or sensitive keywords
                        if (arg instanceof String s) {
                            if (JWT_PATTERN.matcher(s).matches()) {
                                return "***REDACTED_JWT***";
                            }
                            String low = s.toLowerCase();
                            if (low.contains("password") || low.contains("token") || low.contains("secret") || low.contains("jwt")) {
                                return "***REDACTED***";
                            }
                            // long opaque strings (likely tokens) - redact by heuristic
                            if (s.length() > 100 && s.contains(".")) {
                                return "***REDACTED***";
                            }
                            return s;
                        }

                        // For other object types fall back to toString and check
                        String value = arg.toString();
                        if (JWT_PATTERN.matcher(value).matches()) {
                            return "***REDACTED_JWT***";
                        }

                        String lower = value.toLowerCase();
                        if (lower.contains("password") || lower.contains("token") || lower.contains("secret") || lower.contains("jwt")) {
                            return "***REDACTED***";
                        }

                        return arg;
                    } catch (Exception ex) {
                        return "<UNSERIALIZABLE_ARG>";
                    }
                })
                .toList();
    }

    private String redactIfSensitive(String s) {
        if (s == null) return null;
        if (JWT_PATTERN.matcher(s).matches()) return "***REDACTED_JWT***";
        String low = s.toLowerCase();
        if (low.contains("password") || low.contains("token") || low.contains("secret") || low.contains("jwt")) {
            return "***REDACTED***";
        }
        if (s.length() > 100 && s.contains(".")) return "***REDACTED***";
        return s;
    }
}
