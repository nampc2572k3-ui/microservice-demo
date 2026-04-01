package com.example.demo.core.intergration.relay;

import com.example.demo.core.domain.event.internal.RoleAssignedTransactionalEvent;
import com.example.demo.core.domain.event.internal.RoleRevokedTransactionalEvent;
import com.example.demo.core.intergration.publisher.PermissionEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionalKafkaRelay {
    private final PermissionEventPublisher kafkaPublisher;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onRoleAssigned(RoleAssignedTransactionalEvent event) throws JsonProcessingException {
        kafkaPublisher.publishRoleAssigned(event.getAccountId(), event.getRole());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onRoleRevoked(RoleRevokedTransactionalEvent event) throws JsonProcessingException {
        kafkaPublisher.publishRoleRevoked(event.getAccountId(), event.getRole());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onRoleAssignedRollback(RoleAssignedTransactionalEvent event) {
        log.warn("Role assignment rolled back for account {}, no Kafka event sent", event.getAccountId());
    }
}
