package com.use_management_system.user_management.repository;

import com.use_management_system.user_management.entity.RolePermission;
import com.use_management_system.user_management.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {
    List<RolePermission> findByRoleAndActive(Role role, Boolean active);
    List<RolePermission> findByRole(Role role);
}
