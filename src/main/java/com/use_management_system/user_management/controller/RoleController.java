package com.use_management_system.user_management.controller;

import com.use_management_system.user_management.entity.Role;
import com.use_management_system.user_management.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Map<String, String> request) {
        try {
            String roleName = request.get("roleName");
            String description = request.get("description");
            Role role = roleService.createRole(roleName, description);
            return ResponseEntity.status(HttpStatus.CREATED).body(role);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<Role> getRoleById(@PathVariable String roleId) {
        try {
            Role role = roleService.getRoleById(UUID.fromString(roleId));
            return ResponseEntity.ok(role);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/name/{roleName}")
    public ResponseEntity<Role> getRoleByName(@PathVariable String roleName) {
        try {
            Role role = roleService.getRoleByName(roleName);
            return ResponseEntity.ok(role);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/assign")
    public ResponseEntity<Map<String, String>> assignRoleToUser(
            @RequestBody Map<String, String> request) {
        try {
            UUID userId = UUID.fromString(request.get("userId"));
            UUID roleId = UUID.fromString(request.get("roleId"));
            roleService.assignRoleToUser(userId, roleId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Role assigned successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/remove")
    public ResponseEntity<Map<String, String>> removeRoleFromUser(
            @RequestBody Map<String, String> request) {
        try {
            UUID userId = UUID.fromString(request.get("userId"));
            UUID roleId = UUID.fromString(request.get("roleId"));
            roleService.removeRoleFromUser(userId, roleId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Role removed successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Role>> getUserRoles(@PathVariable String userId) {
        try {
            List<Role> roles = roleService.getUserRoles(UUID.fromString(userId));
            return ResponseEntity.ok(roles);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
