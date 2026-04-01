package com.example.demo.core.domain.event.internal;

import lombok.Getter;
import org.apache.kafka.clients.consumer.internals.events.ApplicationEvent;

public class LoginFailedTransactionalEvent extends ApplicationEvent {

    @Getter
    private final String email;

    @Getter
    private final String ipAddress;

    @Getter
    private final String reason;


    public LoginFailedTransactionalEvent(Object source, String email, String ipAddress, String reason) {
        super((Type) source);
        this.email = email;
        this.ipAddress = ipAddress;
        this.reason = reason;
    }

}
