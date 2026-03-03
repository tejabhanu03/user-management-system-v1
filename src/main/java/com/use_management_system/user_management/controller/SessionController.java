package com.use_management_system.user_management.controller;

import com.use_management_system.user_management.entity.Session;
import com.use_management_system.user_management.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<Session>> getUserActiveSessions(@PathVariable String userId) {
        try {
            List<Session> sessions = sessionService.getUserActiveSessions(UUID.fromString(userId));
            return ResponseEntity.ok(sessions);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Session>> getUserAllSessions(@PathVariable String userId) {
        try {
            List<Session> sessions = sessionService.getUserAllSessions(UUID.fromString(userId));
            return ResponseEntity.ok(sessions);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{sessionToken}")
    public ResponseEntity<Session> getSessionByToken(@PathVariable String sessionToken) {
        try {
            Session session = sessionService.getSessionByToken(sessionToken);
            return ResponseEntity.ok(session);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/user/{userId}/invalidate-all")
    public ResponseEntity<Map<String, String>> invalidateAllUserSessions(@PathVariable String userId) {
        try {
            sessionService.invalidateAllUserSessions(UUID.fromString(userId));
            Map<String, String> response = new HashMap<>();
            response.put("message", "All sessions invalidated");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
