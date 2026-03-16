package com.example.demo.domain.service.impl;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomBusinessException;
import com.example.demo.domain.model.dto.request.AssignRoleRequest;
import com.example.demo.domain.model.dto.response.RoleResponse;
import com.example.demo.domain.model.entity.AccountRole;
import com.example.demo.domain.model.entity.Role;
import com.example.demo.domain.repository.AccountRoleRepository;
import com.example.demo.domain.repository.RoleRepository;
import com.example.demo.domain.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;

    @Transactional(readOnly = true)
    @Override
    public List<RoleResponse> getRolesByAccount(String accId) {
        List<RoleResponse> list = roleRepository.findRolesByAccountId(accId);

        // log list for debugging
        log.info("Roles for account {}: {}", accId, list);

        return list;
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

        // Publish event để cache invalidation
        // todo: publish event here

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

        // Publish event để cache invalidation
        // todo: publish event here

    }
}
