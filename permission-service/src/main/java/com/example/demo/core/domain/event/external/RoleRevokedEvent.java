package com.example.demo.core.domain.event.external;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleRevokedEvent {
    private String eventId;
    private String accountId;
    private Long roleId;
    private String roleName;
    private LocalDateTime occurredAt;
}
