package com.example.demo.core.persistence;

import com.example.demo.core.application.dto.projection.PermissionProjection;
import com.example.demo.core.domain.model.entity.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {

    @Modifying
    @Query(value = """
            INSERT IGNORE INTO account_roles(account_id, role_id)
            VALUES (:accountId, :roleId)
            """, nativeQuery = true)
    void insertIgnore(String accountId, Long roleId);

    @Modifying
    @Query(value = """
            DELETE FROM account_roles
            WHERE account_id = :accId
            AND role_id = :roleId
            """, nativeQuery = true)
    void revokeRole(String accId, Long roleId);

    Optional<AccountRole> findByAccountIdAndRoleId(String accId, Long roleId);

    @Query("""
                SELECT r.name as roleName,
                       m.code as menuCode,
                       rm.bitmask as bitmask
                FROM AccountRole ar
                JOIN ar.role r
                JOIN RoleMenu rm ON rm.role.id = r.id
                JOIN rm.menu m
                WHERE ar.accountId = :accountId
            """)
    List<PermissionProjection> getAllPermissions(String accountId);
}
