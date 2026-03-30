package com.example.demo.core.persistence;

import com.example.demo.core.application.dto.response.RoleResponse;
import com.example.demo.core.domain.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("""
            SELECT new com.example.demo.core.application.dto.response.RoleResponse(
                 r.id,
                 r.name,
                 r.description,
                 r.system
            )
            FROM AccountRole ar
            JOIN ar.role r
            WHERE ar.accountId = :accId
            """)
    List<RoleResponse> findRolesByAccountId(@Param("accId") String accId);


    @Query("""
                SELECT new com.example.demo.core.application.dto.response.RoleResponse(
                    r.id,
                    r.name,
                    r.description,
                    r.system
                )
                FROM Role r
                WHERE r.isDeleted = false
            """)
    List<RoleResponse> findAllRoles();


    @Query(value = """
                SELECT COALESCE(BIT_OR(rm.bitmask), 0)
                FROM account_roles ar
                JOIN role_menus rm ON ar.role_id = rm.role_id
                JOIN menus m ON rm.menu_id = m.id
                JOIN menu_resources mr ON m.id = mr.menu_id
                JOIN resources r ON mr.resource_id = r.id
                WHERE ar.account_id = :accId
                  AND ar.is_deleted = false
                  AND r.path_pattern = :path
                  AND r.http_method = :method
            """, nativeQuery = true)
    Integer getMergedBitmask(String accId, String path, String method);



}
