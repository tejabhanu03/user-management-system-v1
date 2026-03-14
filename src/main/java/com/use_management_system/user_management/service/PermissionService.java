package com.use_management_system.user_management.service;

import com.use_management_system.user_management.entity.Client;
import com.use_management_system.user_management.entity.Permission;
import com.use_management_system.user_management.entity.Role;
import com.use_management_system.user_management.entity.RolePermission;
import com.use_management_system.user_management.repository.PermissionRepository;
import com.use_management_system.user_management.repository.RolePermissionRepository;
import com.use_management_system.user_management.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final ClientService clientService;

    public PermissionService(PermissionRepository permissionRepository,
                             RolePermissionRepository rolePermissionRepository,
                             RoleRepository roleRepository,
                             ClientService clientService) {
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.roleRepository = roleRepository;
        this.clientService = clientService;
    }

    public Permission createPermission(String permissionName, String description, UUID clientId) {
        if (permissionRepository.findByPermissionName(permissionName).isPresent()) {
            throw new RuntimeException("Permission already exists");
        }

        Permission permission = new Permission();
        Client client = clientService.resolveClient(clientId);
        permission.setPermissionName(permissionName);
        permission.setDescription(description);
        permission.setClient(client);
        permission.setActive(true);

        return permissionRepository.save(permission);
    }

    public Permission getPermissionById(UUID permissionId) {
        return permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
    }

    public Permission getPermissionByName(String permissionName) {
        return permissionRepository.findByPermissionName(permissionName)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public void assignPermissionToRole(UUID roleId, UUID permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        rolePermission.setActive(true);

        rolePermissionRepository.save(rolePermission);
    }

    public void removePermissionFromRole(UUID roleId, UUID permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        List<RolePermission> rolePermissions = rolePermissionRepository.findByRole(role);
        RolePermission rolePermissionToRemove = rolePermissions.stream()
                .filter(rp -> rp.getPermission().getId().equals(permissionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role permission mapping not found"));

        rolePermissionRepository.delete(rolePermissionToRemove);
    }

    public List<Permission> getRolePermissions(UUID roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        return rolePermissionRepository.findByRoleAndActive(role, true)
                .stream()
                .map(RolePermission::getPermission)
                .collect(Collectors.toList());
    }
}
