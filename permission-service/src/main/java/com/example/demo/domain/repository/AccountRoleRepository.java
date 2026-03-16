package com.example.demo.domain.repository;

import com.example.demo.domain.model.entity.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole,Long> {

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
}
