package com.example.demo.core.adapter.listener;

import com.example.demo.core.domain.event.internal.LoginFailedEvent;
import com.example.demo.core.domain.helper.AuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginFailedEventListener {

    private final AuthHelper authHelper;

    @Async
    @EventListener
    public void onLoginFailedEvent(LoginFailedEvent loginFailedEvent) {
        authHelper.recordLoginAttempt(
                loginFailedEvent.getEmail(),
                loginFailedEvent.getIpAddress(),
                false,
                loginFailedEvent.getReason()
        );

        log.error("Login Failed Event received: {}", loginFailedEvent.getReason());
    }

}
