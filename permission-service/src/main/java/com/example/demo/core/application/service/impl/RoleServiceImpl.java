package com.example.demo.core.application.service.impl;

import com.example.demo.common.cache.core.CacheService;
import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomBusinessException;
import com.example.demo.core.application.cache.RoleCache;
import com.example.demo.core.application.dto.request.AssignRoleRequest;
import com.example.demo.core.application.dto.response.RoleResponse;
import com.example.demo.core.application.service.RoleService;
import com.example.demo.core.domain.event.internal.RoleAssignedTransactionalEvent;
import com.example.demo.core.domain.model.entity.AccountRole;
import com.example.demo.core.domain.model.entity.Role;
import com.example.demo.core.persistence.AccountRoleRepository;
import com.example.demo.core.persistence.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final RoleCache roleCache;

    private final CacheService cacheService;

    @Transactional(readOnly = true)
    @Override
    public List<RoleResponse> getRolesByAccount(String accId) {

        Optional<List<RoleResponse>> cached = roleCache.get(accId);
        if (cached.isPresent()) return cached.get();

        List<RoleResponse> roles = roleRepository.findRolesByAccountId(accId);
        String version = cacheService.getOrInitVersion(accId);

        roleCache.put(accId, version, roles);

        return roles;

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void assignRole(String accId, AssignRoleRequest request) {

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new CustomBusinessException(
                        ErrorCode.ROLE_NOT_FOUND.getCode(),
                        ErrorCode.ROLE_NOT_FOUND.getMessage()
                ));

        accountRoleRepository.insertIgnore(accId, role.getId());

        // Publish event cache invalidation
        eventPublisher.publishEvent(new RoleAssignedTransactionalEvent(this, accId, role) );

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void revokeRole(String accId, Long roleId) {
        AccountRole accountRole = accountRoleRepository.findByAccountIdAndRoleId(accId, roleId)
                .orElseThrow(() -> new CustomBusinessException(
                        ErrorCode.ROLE_UNASSIGNED.getCode(),
                        ErrorCode.ROLE_UNASSIGNED.getMessage()
                ));

        accountRoleRepository.revokeRole(accId, roleId);

        // Publish event cache invalidation
        eventPublisher.publishEvent(new RoleAssignedTransactionalEvent(this, accId, accountRole.getRole()) );

    }

    @Override
    public List<RoleResponse> getAll() {
        return roleRepository.findAllRoles();
    }
}
