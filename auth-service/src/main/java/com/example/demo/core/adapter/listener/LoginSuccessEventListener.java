package com.example.demo.core.adapter.listener;

import com.example.demo.core.domain.event.internal.LoginSuccessEvent;
import com.example.demo.core.domain.helper.AuthHelper;
import com.example.demo.core.persistence.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginSuccessEventListener {

    private final AuthHelper authHelper;

    private final AccountRepository accountRepository;

    @Async
    @EventListener
    public void onLoginSuccessEvent(LoginSuccessEvent loginSuccessEvent) {

        // update last login time and record login attempt
        loginSuccessEvent.getAccount().setLastLoginAt(LocalDateTime.now());
        accountRepository.save(loginSuccessEvent.getAccount());

        authHelper.recordLoginAttempt(
                loginSuccessEvent.getAccount().getEmail(),
                loginSuccessEvent.getClientIp(),
                true,
                null
        );

        // log success
        log.info("Account {} logged in successfully", loginSuccessEvent.getAccount().getEmail());

    }

}
