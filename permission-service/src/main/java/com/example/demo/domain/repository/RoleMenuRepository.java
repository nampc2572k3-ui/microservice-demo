package com.example.demo.domain.repository;

import com.example.demo.domain.model.entity.RoleMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleMenuRepository extends JpaRepository<RoleMenu, Long> {


    @Modifying
    @Query(value = """
    INSERT INTO role_menus (role_id, menu_id, bitmask)
    VALUES (:roleId, :menuId, :bitmask)
    ON DUPLICATE KEY UPDATE
        bitmask = VALUES(bitmask)
""", nativeQuery = true)
    void upsertRoleMenu(Long roleId, Long menuId, int bitmask);

}
