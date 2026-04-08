package com.example.demo.core.intergration.relay;

import com.example.demo.core.domain.event.internal.PermissionChangedEvent;
import com.example.demo.core.domain.event.internal.RoleAssignedTransactionalEvent;
import com.example.demo.core.domain.event.internal.RoleRevokedTransactionalEvent;
import com.example.demo.core.intergration.publisher.PermissionEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionalKafkaRelay {

    private final PermissionEventPublisher permissionEventPublisher;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onRoleAssigned(RoleAssignedTransactionalEvent event) throws JsonProcessingException {
        permissionEventPublisher.publishRoleAssigned(event.getAccountId(), event.getRole());
        eventPublisher.publishEvent(new PermissionChangedEvent(event.getAccountId()));


    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onRoleRevoked(RoleRevokedTransactionalEvent event) throws JsonProcessingException {
        permissionEventPublisher.publishRoleRevoked(event.getAccountId(), event.getRole());
        eventPublisher.publishEvent(new PermissionChangedEvent(event.getAccountId()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onRoleAssignedRollback(RoleAssignedTransactionalEvent event) {
        log.warn("Role assignment rolled back for account {}, no Kafka event sent", event.getAccountId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onRoleRevokedRollback(RoleRevokedTransactionalEvent event) {
        log.warn("Role revoked for account {}, no Kafka event sent", event.getAccountId());
    }
}
