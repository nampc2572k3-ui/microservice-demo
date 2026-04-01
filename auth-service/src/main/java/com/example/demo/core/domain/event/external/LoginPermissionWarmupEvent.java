package com.example.demo.core.domain.event.external;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginPermissionWarmupEvent {
    private String accountId;
    private String eventId;

}
