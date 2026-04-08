package com.example.demo.core.domain.event.internal;

import com.example.demo.core.domain.model.entity.Account;
import lombok.Getter;
import org.apache.kafka.clients.consumer.internals.events.ApplicationEvent;

@Getter
public class RegisterSuccessTransactionalEvent extends ApplicationEvent {

    private final Account account;

    public RegisterSuccessTransactionalEvent(
            Object source, Account account
    ) {
        super((ApplicationEvent.Type) source);
        this.account = account;
    }

}
