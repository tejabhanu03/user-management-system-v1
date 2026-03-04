package com.use_management_system.user_management.controller;

import com.use_management_system.user_management.dto.UserContextDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-context")
public class UserContextController {

    @GetMapping("/me")
    public ResponseEntity<UserContextDto> getCurrentUserContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserContextDto userContext)) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(userContext);
    }
}

