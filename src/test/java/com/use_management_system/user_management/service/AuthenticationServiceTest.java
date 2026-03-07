package com.use_management_system.user_management.service;

import com.use_management_system.user_management.dto.LoginRequest;
import com.use_management_system.user_management.entity.User;
import com.use_management_system.user_management.exception.EmailVerificationRequiredException;
import com.use_management_system.user_management.repository.RolePermissionRepository;
import com.use_management_system.user_management.repository.SessionRepository;
import com.use_management_system.user_management.repository.UserRepository;
import com.use_management_system.user_management.repository.UserRoleRepository;
import com.use_management_system.user_management.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(
                userRepository,
                sessionRepository,
                userRoleRepository,
                rolePermissionRepository,
                passwordEncoder,
                jwtTokenUtil
        );
    }

    @Test
    void loginShouldFailWhenEmailNotVerified() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("john");
        user.setPassword("encoded");
        user.setEmailVerified(false);
        user.setActive(true);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded")).thenReturn(true);

        assertThrows(
                EmailVerificationRequiredException.class,
                () -> authenticationService.login(new LoginRequest("john", "password123"), "127.0.0.1", "JUnit")
        );
    }
}
