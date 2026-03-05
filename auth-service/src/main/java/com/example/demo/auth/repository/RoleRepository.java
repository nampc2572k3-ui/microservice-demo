package com.example.demo.auth.repository;

import com.example.demo.auth.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT DISTINCT r FROM Role r " +
            "LEFT JOIN FETCH r.roleMenus rm " +
            "LEFT JOIN FETCH rm.menu m " +
            "WHERE r.id IN :ids")
    List<Role> findAllByIdWithMenus(@Param("ids") Collection<Long> ids);


}
