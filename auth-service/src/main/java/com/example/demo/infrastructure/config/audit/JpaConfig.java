package com.example.demo.infrastructure.config.audit;

import com.example.demo.common.utils.UserInfoUtils;
import com.example.demo.core.domain.model.entity.Account;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {

        return () -> {
            Account currentAcc = UserInfoUtils.getCurrentAccount();

            if (currentAcc != null) {
                return Optional.of(currentAcc.getUsername());
            } else {
                return Optional.of("SYSTEM");
            }
        };

    }

}
