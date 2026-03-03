package com.example.demo.core.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final HttpServletRequest request;

    // Chỉ log controller + service
    private static final String POINTCUT =
            "execution(* com.lgcns.amfw..controller..*(..)) || " +
                    "execution(* com.lgcns.amfw..service..*(..))";

    @Around(POINTCUT)
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        long startNanos = System.nanoTime();

        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String username = getCurrentUsername();


        String httpMethod = "NO_HTTP";
        String requestUri = "NO_HTTP";
        try {
            if (request != null) {
                httpMethod = request.getMethod();
                requestUri = request.getRequestURI();
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "anonymous";
        }
        return auth.getName();
    }

    private Object sanitizeArgs(Object[] args) {
        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) return null;

                    String value = arg.toString().toLowerCase();

                    if (value.contains("password") || value.contains("token")) {
                        return "***";
                    }

                    return arg;
                })
                .toList();
    }
}