package com.example.demo.core.domain.event.external;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterSuccessEvent {

    private String accountId;
    private String username;
    private String eventId;

}
