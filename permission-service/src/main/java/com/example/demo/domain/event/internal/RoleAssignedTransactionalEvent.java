package com.example.demo.domain.event.internal;

import com.example.demo.domain.model.entity.Role;
import lombok.Getter;
import org.apache.kafka.clients.consumer.internals.events.ApplicationEvent;

public class RoleAssignedTransactionalEvent extends ApplicationEvent {
    @Getter
    private final String accountId;
    @Getter
    private final Role role;

    public RoleAssignedTransactionalEvent(Object source, String accountId, Role role) {
        super((Type) source);
        this.accountId = accountId;
        this.role = role;
    }
}