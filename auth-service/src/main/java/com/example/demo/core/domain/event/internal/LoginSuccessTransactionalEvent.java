package com.example.demo.core.domain.event.internal;

import com.example.demo.core.domain.model.entity.Account;
import com.example.demo.core.domain.model.entity.AccountDevice;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.apache.kafka.clients.consumer.internals.events.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class LoginSuccessTransactionalEvent extends ApplicationEvent {

    private final Account account;

    private final String refreshToken;

    private final AccountDevice devide;

    private final String clientIp;

    private final HttpServletRequest request;

    private final LocalDateTime loginTime;

    public LoginSuccessTransactionalEvent(
            Account account, String refreshToken,
            AccountDevice devide, String clientIp,
            HttpServletRequest request, Object source
    ) {
        super((Type) source);
        this.account = account;
        this.refreshToken = refreshToken;
        this.devide = devide;
        this.clientIp = clientIp;
        this.request = request;
        this.loginTime = LocalDateTime.now();
    }

}
