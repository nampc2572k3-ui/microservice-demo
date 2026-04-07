package com.example.demo.core.intergration.relay;


import com.example.demo.core.domain.event.internal.LoginSuccessEvent;
import com.example.demo.core.domain.event.internal.LoginSuccessTransactionalEvent;
import com.example.demo.core.intergration.publisher.LoginEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionalKafkaRelay {

    private final LoginEventPublisher loginEventPublisher;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onLoginSuccess(LoginSuccessTransactionalEvent event) throws JsonProcessingException {
        loginEventPublisher.publishLoginSuccess(
                event.getAccount()
        );

        eventPublisher.publishEvent(
                new LoginSuccessEvent(
                        event.getAccount(), event.getRefreshToken(),
                        event.getDevide(), event.getClientIp(),
                        event.getRequest(), LocalDateTime.now()
                )
        );
    }


}
