package com.example.demo.core.domain.event.internal;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginFailedEvent {

    private String email;

    private String ipAddress;

    private String reason;

}
