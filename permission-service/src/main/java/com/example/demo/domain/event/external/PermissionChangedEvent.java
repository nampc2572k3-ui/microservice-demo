package com.example.demo.domain.event.external;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionChangedEvent {
    private String eventId;
    private String accountId;
    private String changeType;
    private LocalDateTime occurredAt;
}
