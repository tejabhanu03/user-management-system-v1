package com.use_management_system.user_management.service;

import com.use_management_system.user_management.entity.Role;
import com.use_management_system.user_management.entity.User;
import com.use_management_system.user_management.entity.UserRole;
import com.use_management_system.user_management.repository.RoleRepository;
import com.use_management_system.user_management.repository.UserRepository;
import com.use_management_system.user_management.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public Role createRole(String roleName, String description) {
        if (roleRepository.findByRoleName(roleName).isPresent()) {
            throw new RuntimeException("Role already exists");
        }

        Role role = new Role();
        role.setRoleName(roleName);
        role.setDescription(description);
        role.setActive(true);

        return roleRepository.save(role);
    }

    public Role getRoleById(UUID roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    public Role getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public void assignRoleToUser(UUID userId, UUID roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setActive(true);

        userRoleRepository.save(userRole);
    }

    public void removeRoleFromUser(UUID userId, UUID roleId) {
        // Find and remove the UserRole entry
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserRole> userRoles = userRoleRepository.findByUser(user);
        UserRole userRoleToRemove = userRoles.stream()
                .filter(ur -> ur.getRole().getId().equals(roleId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User role mapping not found"));

        userRoleRepository.delete(userRoleToRemove);
    }

    public List<Role> getUserRoles(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userRoleRepository.findByUserAndActive(user, true)
                .stream()
                .map(UserRole::getRole)
                .collect(Collectors.toList());
    }
}
