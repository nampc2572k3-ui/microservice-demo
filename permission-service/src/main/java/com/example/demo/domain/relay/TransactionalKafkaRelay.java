package com.example.demo.domain.relay;


import com.example.demo.domain.event.internal.RoleAssignedTransactionalEvent;
import com.example.demo.domain.event.internal.RoleRevokedTransactionalEvent;
import com.example.demo.domain.publisher.PermissionEventPublisher;
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

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoleAssigned(RoleAssignedTransactionalEvent event) {
        kafkaPublisher.publishRoleAssigned(event.getAccountId(), event.getRole());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoleRevoked(RoleRevokedTransactionalEvent event) {
        kafkaPublisher.publishRoleRevoked(event.getAccountId(), event.getRole());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onRoleAssignedRollback(RoleAssignedTransactionalEvent event) {
        log.warn("Role assignment rolled back for account {}, no Kafka event sent", event.getAccountId());
    }
}
