package com.use_management_system.user_management.service;

import com.use_management_system.user_management.entity.Session;
import com.use_management_system.user_management.entity.User;
import com.use_management_system.user_management.repository.SessionRepository;
import com.use_management_system.user_management.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public SessionService(SessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    public List<Session> getUserActiveSessions(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return sessionRepository.findByUserAndActive(user, true);
    }

    public List<Session> getUserAllSessions(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return sessionRepository.findByUser(user);
    }

    public Session getSessionByToken(String sessionToken) {
        return sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }

    public void invalidateAllUserSessions(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Session> activeSessions = sessionRepository.findByUserAndActive(user, true);
        activeSessions.forEach(session -> {
            session.setActive(false);
            session.setLogoutAt(LocalDateTime.now());
        });
        sessionRepository.saveAll(activeSessions);
    }

    public void updateSessionActivity(String sessionToken) {
        Session session = sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        session.setLastActivityAt(LocalDateTime.now());
        sessionRepository.save(session);
    }
}
