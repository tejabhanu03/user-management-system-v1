package com.use_management_system.user_management.service;

import com.use_management_system.user_management.dto.LoginRequest;
import com.use_management_system.user_management.dto.LoginResponse;
import com.use_management_system.user_management.entity.Session;
import com.use_management_system.user_management.entity.User;
import com.use_management_system.user_management.repository.SessionRepository;
import com.use_management_system.user_management.repository.UserRepository;
import com.use_management_system.user_management.util.TokenUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository,
                                 SessionRepository sessionRepository,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request, String ipAddress, String userAgent) {
        User user = userRepository.findByUsernameAndActive(request.getUsername(), true)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String sessionToken = TokenUtil.generateToken();

        Session session = new Session();
        session.setUser(user);
        session.setSessionToken(sessionToken);
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setActive(true);

        sessionRepository.save(session);

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                sessionToken,
                "Login successful"
        );
    }

    public void logout(String sessionToken) {
        Session session = sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new RuntimeException("Invalid session"));

        session.setActive(false);
        session.setLogoutAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    public boolean validateSession(String sessionToken) {
        return sessionRepository.findBySessionToken(sessionToken)
                .map(session -> session.getActive() && session.getLogoutAt() == null)
                .orElse(false);
    }

    public User getUserFromSession(String sessionToken) {
        Session session = sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new RuntimeException("Invalid session"));

        if (!session.getActive() || session.getLogoutAt() != null) {
            throw new RuntimeException("Session is inactive");
        }

        session.setLastActivityAt(LocalDateTime.now());
        sessionRepository.save(session);

        return session.getUser();
    }
}

