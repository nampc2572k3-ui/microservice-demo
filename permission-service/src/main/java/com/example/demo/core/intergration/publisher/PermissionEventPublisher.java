package com.example.demo.core.intergration.publisher;

import com.example.demo.core.domain.event.external.PermissionChangedEvent;
import com.example.demo.core.domain.event.external.RoleAssignedEvent;
import com.example.demo.core.domain.event.external.RoleRevokedEvent;
import com.example.demo.core.domain.model.entity.Role;
import com.example.demo.infrastructure.outbox.OutboxWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
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

    private final OutboxWriter outboxWriter;

    @Value("${app.kafka.topics.permission-changed}")
    private String permissionChangedTopic;

    @Value("${app.kafka.topics.role-assigned}")
    private String roleAssignedTopic;

    @Value("${app.kafka.topics.role-revoked}")
    private String roleRevokedTopic;


    public void publishRoleRevoked(String accId, Role role) throws JsonProcessingException {
        outboxWriter.write(
                roleRevokedTopic,
                accId,
                RoleRevokedEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .accountId(accId)
                        .roleName(role.getName())
                        .occurredAt(LocalDateTime.now())
                        .build()
        );

        outboxWriter.write(permissionChangedTopic, accId,
                buildPermisionChanged(accId, "ROLE_REVOKED"));
    }

    public void publishRoleAssigned(String accId, Role role) throws JsonProcessingException {
        outboxWriter.write(
                roleAssignedTopic,
                accId,
                RoleAssignedEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .accountId(accId)
                        .roleName(role.getName())
                        .occurredAt(LocalDateTime.now())
                        .build()
        );

        outboxWriter.write(permissionChangedTopic, accId,
                buildPermisionChanged(accId, "ROLE_ASSIGNED"));
    }


    public PermissionChangedEvent buildPermisionChanged(String accId, String type) {
        return PermissionChangedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .accountId(accId)
                .changeType(type)
                .build();
    }


}