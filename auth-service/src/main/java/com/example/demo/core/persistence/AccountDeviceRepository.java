package com.example.demo.core.persistence;

import com.example.demo.core.domain.model.entity.AccountDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountDeviceRepository extends JpaRepository<AccountDevice, Long> {
    Optional<AccountDevice> findByIdAndAccountId(Long deviceId, String accId);
}
