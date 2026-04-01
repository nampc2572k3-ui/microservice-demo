package com.example.demo.core.domain.event.internal;

import com.example.demo.core.domain.model.entity.Account;
import com.example.demo.core.domain.model.entity.AccountDevice;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginSuccessEvent {

    private Account account;
    private String refreshToken;
    AccountDevice devide;
    private String clientIp;
    private HttpServletRequest request;
    private LocalDateTime loginTime;

}
