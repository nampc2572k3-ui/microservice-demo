package com.example.demo.core.domain.event.internal;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginSuccessEvent {

    private String accountId;
    private String email;
    private String deviceId;
    private String clientIp;
    private String userAgent;
    private LocalDateTime loginTime;
    private String eventId;

}
