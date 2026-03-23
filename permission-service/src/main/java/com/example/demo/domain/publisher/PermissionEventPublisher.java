package com.example.demo.domain.publisher;

import com.example.demo.domain.event.external.PermissionChangedEvent;
import com.example.demo.domain.event.external.RoleAssignedEvent;
import com.example.demo.domain.event.external.RoleRevokedEvent;
import com.example.demo.domain.model.entity.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionEventPublisher {


    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.permission-changed}")
    private String permissionChangedTopic;

    @Value("${app.kafka.topics.role-assigned}")
    private String roleAssignedTopic;

    @Value("${app.kafka.topics.role-revoked}")
    private String roleRevokedTopic;

    private void send(String topic, String key, Object payload) {
        kafkaTemplate.send(topic, key, payload).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send Kafka event to topic {}: {}", topic, ex.getMessage());
            } else {
                log.debug("Sent event to topic {} partition {} offset {}", topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            }
        });
    }

    private void sendPermissionChanged(String accId, String changeType) {
        PermissionChangedEvent event = PermissionChangedEvent.builder().eventId(UUID.randomUUID().toString()).accountId(accId).changeType(changeType).occurredAt(LocalDateTime.now()).build();
        send(permissionChangedTopic, accId, event);
    }

    public void publishBitmaskUpdated(String accId) {
        sendPermissionChanged(accId, "BITMASK_UPDATED");
    }

    public void publishRoleRevoked(String accId, Role role) {
        RoleRevokedEvent event = RoleRevokedEvent.builder().eventId(UUID.randomUUID().toString()).accountId(accId).roleId(role.getId()).roleName(role.getName()).occurredAt(LocalDateTime.now()).build();
        send(roleRevokedTopic, accId, event);
        sendPermissionChanged(accId, "ROLE_REVOKED");
    }

    public void publishRoleAssigned(String accountId, Role role) {
        RoleAssignedEvent event = RoleAssignedEvent.builder().eventId(UUID.randomUUID().toString()).accountId(accountId).roleId(role.getId()).roleName(role.getName()).occurredAt(LocalDateTime.now()).build();
        send(roleAssignedTopic, accountId, event);
        sendPermissionChanged(accountId, "ROLE_ASSIGNED");
    }
}