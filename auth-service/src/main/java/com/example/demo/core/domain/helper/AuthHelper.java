package com.example.demo.core.domain.helper;

import com.example.demo.core.application.dto.request.LoginRequest;
import com.example.demo.core.domain.model.entity.Account;
import com.example.demo.core.domain.model.entity.AccountDevice;
import com.example.demo.core.domain.model.enums.Platform;
import com.example.demo.core.persistence.AccountDeviceRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthHelper {

    private final AccountDeviceRepository accountDeviceRepository;

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



}
