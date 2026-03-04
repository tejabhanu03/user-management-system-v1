package com.use_management_system.user_management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Example controller demonstrating how downstream services can protect endpoints
 * using role and permission-based authorization derived from the JWT.
 */
@RestController
@RequestMapping("/api/sample-resource")
public class SampleProtectedResourceController {

    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> adminOnlyEndpoint() {
        return ResponseEntity.ok(Map.of("message", "Accessible only to ADMIN role"));
    }

    @GetMapping("/user-read")
    @PreAuthorize("hasAuthority('PERM_user.read')")
    public ResponseEntity<Map<String, String>> userReadEndpoint() {
        return ResponseEntity.ok(Map.of("message", "Accessible to tokens with 'user.read' permission"));
    }
}

