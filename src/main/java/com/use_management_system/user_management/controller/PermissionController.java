package com.use_management_system.user_management.controller;

import com.use_management_system.user_management.entity.Permission;
import com.use_management_system.user_management.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<Permission> createPermission(@RequestBody Map<String, String> request) {
        try {
            String permissionName = request.get("permissionName");
            String description = request.get("description");
            Permission permission = permissionService.createPermission(permissionName, description);
            return ResponseEntity.status(HttpStatus.CREATED).body(permission);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{permissionId}")
    public ResponseEntity<Permission> getPermissionById(@PathVariable String permissionId) {
        try {
            Permission permission = permissionService.getPermissionById(UUID.fromString(permissionId));
            return ResponseEntity.ok(permission);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/name/{permissionName}")
    public ResponseEntity<Permission> getPermissionByName(@PathVariable String permissionName) {
        try {
            Permission permission = permissionService.getPermissionByName(permissionName);
            return ResponseEntity.ok(permission);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Permission>> getAllPermissions() {
        List<Permission> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @PostMapping("/assign")
    public ResponseEntity<Map<String, String>> assignPermissionToRole(
            @RequestBody Map<String, String> request) {
        try {
            UUID roleId = UUID.fromString(request.get("roleId"));
            UUID permissionId = UUID.fromString(request.get("permissionId"));
            permissionService.assignPermissionToRole(roleId, permissionId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Permission assigned to role successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/remove")
    public ResponseEntity<Map<String, String>> removePermissionFromRole(
            @RequestBody Map<String, String> request) {
        try {
            UUID roleId = UUID.fromString(request.get("roleId"));
            UUID permissionId = UUID.fromString(request.get("permissionId"));
            permissionService.removePermissionFromRole(roleId, permissionId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Permission removed from role successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<Permission>> getRolePermissions(@PathVariable String roleId) {
        try {
            List<Permission> permissions = permissionService.getRolePermissions(UUID.fromString(roleId));
            return ResponseEntity.ok(permissions);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
