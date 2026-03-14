package com.use_management_system.user_management.service;

import com.use_management_system.user_management.dto.LoginRequest;
import com.use_management_system.user_management.dto.LoginResponse;
import com.use_management_system.user_management.dto.UserContextDto;
import com.use_management_system.user_management.entity.RolePermission;
import com.use_management_system.user_management.entity.Session;
import com.use_management_system.user_management.entity.User;
import com.use_management_system.user_management.entity.UserRole;
import com.use_management_system.user_management.exception.EmailVerificationRequiredException;
import com.use_management_system.user_management.repository.RolePermissionRepository;
import com.use_management_system.user_management.repository.SessionRepository;
import com.use_management_system.user_management.repository.UserRepository;
import com.use_management_system.user_management.repository.UserRoleRepository;
import com.use_management_system.user_management.util.JwtTokenUtil;
import com.use_management_system.user_management.util.TokenUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthenticationService(UserRepository userRepository,
                                 SessionRepository sessionRepository,
                                 UserRoleRepository userRoleRepository,
                                 RolePermissionRepository rolePermissionRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.userRoleRepository = userRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public LoginResponse login(LoginRequest request, String ipAddress, String userAgent) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new EmailVerificationRequiredException();
        }

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new RuntimeException("User account is inactive");
        }

        String sessionToken = TokenUtil.generateToken();

        com.use_management_system.user_management.entity.Session session =
                new com.use_management_system.user_management.entity.Session();
        session.setUser(user);
        session.setClient(user.getClient());
        session.setSessionToken(sessionToken);
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setActive(true);

        sessionRepository.save(session);

        UserContextDto userContext = buildUserContext(user);
        String jwtToken = jwtTokenUtil.generateToken(userContext);

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                sessionToken,
                jwtToken,
                userContext.getRoles(),
                userContext.getPermissions(),
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

    private UserContextDto buildUserContext(User user) {
        var activeUserRoles = userRoleRepository.findByUserAndActive(user, true);

        var roles = activeUserRoles.stream()
                .map(UserRole::getRole)
                .filter(role -> Boolean.TRUE.equals(role.getActive()))
                .map(role -> role.getRoleName().toUpperCase())
                .distinct()
                .toList();

        var permissions = activeUserRoles.stream()
                .flatMap(userRole -> rolePermissionRepository.findByRoleAndActive(userRole.getRole(), true).stream())
                .map(RolePermission::getPermission)
                .filter(permission -> Boolean.TRUE.equals(permission.getActive()))
                .map(permission -> permission.getPermissionName())
                .distinct()
                .toList();

        return new UserContextDto(
                user.getId(),
                user.getClient() == null ? null : user.getClient().getId(),
                user.getUsername(),
                roles,
                permissions
        );
    }
}
