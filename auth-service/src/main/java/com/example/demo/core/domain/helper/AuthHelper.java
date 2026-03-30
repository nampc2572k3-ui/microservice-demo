package com.example.demo.core.domain.helper;

import com.example.demo.core.application.dto.request.LoginRequest;
import com.example.demo.core.domain.model.entity.Account;
import com.example.demo.core.domain.model.entity.AccountDevice;
import com.example.demo.core.domain.model.entity.LoginAttempt;
import com.example.demo.core.domain.model.enums.Platform;
import com.example.demo.core.persistence.AccountDeviceRepository;
import com.example.demo.core.persistence.LoginAttemptRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthHelper {

    private final AccountDeviceRepository accountDeviceRepository;
    private final LoginAttemptRepository loginAttemptRepository;

    public String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

    @Transactional(rollbackFor = Exception.class)
    public AccountDevice handleAccountDevice(LoginRequest request, Account acc) {

        Optional<AccountDevice> optionalDevice =
                accountDeviceRepository.findByIdAndAccountId(
                        request.getDeviceId(),
                        acc.getId()
                );

        AccountDevice device;

        if (optionalDevice.isPresent()) {

            device = optionalDevice.get();
            device.setDeviceName(request.getDeviceName());
            device.setPlatform(Platform.valueOf(request.getPlatform()));
            device.setPushToken(request.getPushToken());
            device.setLastActiveAt(LocalDateTime.now());

        } else {
            device = AccountDevice.builder()
                    .id(request.getDeviceId())
                    .deviceName(request.getDeviceName())
                    .platform(Platform.valueOf(request.getPlatform()))
                    .pushToken(request.getPushToken())
                    .trusted(false)
                    .lastActiveAt(LocalDateTime.now())
                    .account(acc)
                    .build();
        }

        accountDeviceRepository.save(device);

        return device;
    }

    @Transactional(rollbackFor = Exception.class)
    public void recordLoginAttempt(String email, String clientIp, boolean successful, String reason) {
        LoginAttempt attempt = LoginAttempt.builder()
                .email(email)
                .ipAddress(clientIp)
                .successful(successful)
                .failureReason(reason)
                .attemptedAt(LocalDateTime.now())
                .build();
        loginAttemptRepository.save(attempt);
    }

}
