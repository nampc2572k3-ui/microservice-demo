package com.example.demo.core.domain.event.internal;

import com.example.demo.core.domain.model.entity.Role;
import lombok.Getter;
import org.apache.kafka.clients.consumer.internals.events.ApplicationEvent;

public class RoleRevokedTransactionalEvent extends ApplicationEvent {
    @Getter
    private final String accountId;
    @Getter
    private final Role role;

    public RoleRevokedTransactionalEvent(Object source, String accountId, Role role) {
        super((Type) source);
        this.accountId = accountId;
        this.role = role;
    }
}
