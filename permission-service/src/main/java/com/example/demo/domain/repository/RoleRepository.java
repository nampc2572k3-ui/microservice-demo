package com.example.demo.domain.repository;

import com.example.demo.domain.model.dto.response.RoleResponse;
import com.example.demo.domain.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("""
            SELECT new com.example.demo.domain.model.dto.response.RoleResponse(
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
                SELECT new com.example.demo.domain.model.dto.response.RoleResponse(
                    r.id,
                    r.name,
                    r.description,
                    r.system
                )
                FROM Role r
                WHERE r.isDeleted = false
            """)
    List<RoleResponse> findAllRoles();
}
