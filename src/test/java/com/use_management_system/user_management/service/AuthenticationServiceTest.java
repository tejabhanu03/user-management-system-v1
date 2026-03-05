package com.use_management_system.user_management.service;

import com.use_management_system.user_management.dto.LoginRequest;
import com.use_management_system.user_management.dto.LoginResponse;
import com.use_management_system.user_management.dto.UserContextDto;
import com.use_management_system.user_management.entity.*;
import com.use_management_system.user_management.repository.*;
import com.use_management_system.user_management.util.JwtTokenUtil;
import com.use_management_system.user_management.util.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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

    @InjectMocks
    private AuthenticationService authenticationService;

    private UUID userId;
    private UUID roleId;
    private User testUser;
    private LoginRequest loginRequest;
    private Session testSession;
    private Role testRole;
    private Permission testPermission;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        roleId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword123");
        testUser.setFullName("Test User");
        testUser.setActive(true);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        testSession = new Session();
        testSession.setId(UUID.randomUUID());
        testSession.setUser(testUser);
        testSession.setSessionToken("sessionToken123");
        testSession.setIpAddress("127.0.0.1");
        testSession.setUserAgent("test-agent");
        testSession.setActive(true);

        testRole = new Role();
        testRole.setId(roleId);
        testRole.setRoleName("ADMIN");
        testRole.setActive(true);

        testPermission = new Permission();
        testPermission.setId(UUID.randomUUID());
        testPermission.setPermissionName("VIEW_USERS");
        testPermission.setActive(true);
    }

    @Test
    void testLoginSuccess() {
        // Arrange
        when(userRepository.findByUsernameAndActive(loginRequest.getUsername(), true))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword()))
            .thenReturn(true);
        when(sessionRepository.save(any(Session.class))).thenReturn(testSession);
        when(userRoleRepository.findByUserAndActive(testUser, true)).thenReturn(Collections.emptyList());
        when(jwtTokenUtil.generateToken(any(UserContextDto.class))).thenReturn("jwtToken123");

        // Act
        LoginResponse response = authenticationService.login(loginRequest, "127.0.0.1", "test-agent");

        // Assert
        assertNotNull(response);
        assertEquals(testUser.getId(), response.getUserId());
        assertEquals(testUser.getUsername(), response.getUsername());
        assertEquals(testUser.getEmail(), response.getEmail());
        assertNotNull(response.getSessionToken());
        assertEquals("jwtToken123", response.getJwtToken());
        assertEquals("Login successful", response.getMessage());

        verify(userRepository, times(1)).findByUsernameAndActive(loginRequest.getUsername(), true);
        verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), testUser.getPassword());
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    void testLoginWithInvalidUsername() {
        // Arrange
        when(userRepository.findByUsernameAndActive(loginRequest.getUsername(), true))
            .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authenticationService.login(loginRequest, "127.0.0.1", "test-agent"));
        assertEquals("Invalid username or password", exception.getMessage());

        verify(userRepository, times(1)).findByUsernameAndActive(loginRequest.getUsername(), true);
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    void testLoginWithInvalidPassword() {
        // Arrange
        when(userRepository.findByUsernameAndActive(loginRequest.getUsername(), true))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword()))
            .thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authenticationService.login(loginRequest, "127.0.0.1", "test-agent"));
        assertEquals("Invalid username or password", exception.getMessage());

        verify(userRepository, times(1)).findByUsernameAndActive(loginRequest.getUsername(), true);
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    void testLoginWithRolesAndPermissions() {
        // Arrange
        UserRole userRole = new UserRole();
        userRole.setUser(testUser);
        userRole.setRole(testRole);
        userRole.setActive(true);

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(testRole);
        rolePermission.setPermission(testPermission);
        rolePermission.setActive(true);

        when(userRepository.findByUsernameAndActive(loginRequest.getUsername(), true))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword()))
            .thenReturn(true);
        when(sessionRepository.save(any(Session.class))).thenReturn(testSession);
        when(userRoleRepository.findByUserAndActive(testUser, true)).thenReturn(Arrays.asList(userRole));
        when(rolePermissionRepository.findByRoleAndActive(testRole, true))
            .thenReturn(Arrays.asList(rolePermission));
        when(jwtTokenUtil.generateToken(any(UserContextDto.class))).thenReturn("jwtToken123");

        // Act
        LoginResponse response = authenticationService.login(loginRequest, "127.0.0.1", "test-agent");

        // Assert
        assertNotNull(response);
        assertNotNull(response.getRoles());
        assertNotNull(response.getPermissions());

        verify(userRoleRepository, times(1)).findByUserAndActive(testUser, true);
        verify(rolePermissionRepository, times(1)).findByRoleAndActive(testRole, true);
    }

    @Test
    void testLogoutSuccess() {
        // Arrange
        when(sessionRepository.findBySessionToken("sessionToken123"))
            .thenReturn(Optional.of(testSession));
        when(sessionRepository.save(any(Session.class))).thenReturn(testSession);

        // Act
        authenticationService.logout("sessionToken123");

        // Assert
        assertFalse(testSession.getActive());
        assertNotNull(testSession.getLogoutAt());

        verify(sessionRepository, times(1)).findBySessionToken("sessionToken123");
        verify(sessionRepository, times(1)).save(testSession);
    }

    @Test
    void testLogoutWithInvalidSession() {
        // Arrange
        when(sessionRepository.findBySessionToken("invalidToken"))
            .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authenticationService.logout("invalidToken"));
        assertEquals("Invalid session", exception.getMessage());

        verify(sessionRepository, times(1)).findBySessionToken("invalidToken");
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    void testValidateSessionSuccess() {
        // Arrange
        testSession.setActive(true);
        testSession.setLogoutAt(null);
        when(sessionRepository.findBySessionToken("sessionToken123"))
            .thenReturn(Optional.of(testSession));

        // Act
        boolean isValid = authenticationService.validateSession("sessionToken123");

        // Assert
        assertTrue(isValid);
        verify(sessionRepository, times(1)).findBySessionToken("sessionToken123");
    }

    @Test
    void testValidateSessionInactive() {
        // Arrange
        testSession.setActive(false);
        when(sessionRepository.findBySessionToken("sessionToken123"))
            .thenReturn(Optional.of(testSession));

        // Act
        boolean isValid = authenticationService.validateSession("sessionToken123");

        // Assert
        assertFalse(isValid);
        verify(sessionRepository, times(1)).findBySessionToken("sessionToken123");
    }

    @Test
    void testValidateSessionWithLogout() {
        // Arrange
        testSession.setActive(true);
        testSession.setLogoutAt(LocalDateTime.now());
        when(sessionRepository.findBySessionToken("sessionToken123"))
            .thenReturn(Optional.of(testSession));

        // Act
        boolean isValid = authenticationService.validateSession("sessionToken123");

        // Assert
        assertFalse(isValid);
        verify(sessionRepository, times(1)).findBySessionToken("sessionToken123");
    }

    @Test
    void testValidateSessionNotFound() {
        // Arrange
        when(sessionRepository.findBySessionToken("invalidToken"))
            .thenReturn(Optional.empty());

        // Act
        boolean isValid = authenticationService.validateSession("invalidToken");

        // Assert
        assertFalse(isValid);
        verify(sessionRepository, times(1)).findBySessionToken("invalidToken");
    }

    @Test
    void testGetUserFromSessionSuccess() {
        // Arrange
        testSession.setActive(true);
        testSession.setLogoutAt(null);
        when(sessionRepository.findBySessionToken("sessionToken123"))
            .thenReturn(Optional.of(testSession));
        when(sessionRepository.save(any(Session.class))).thenReturn(testSession);

        // Act
        User user = authenticationService.getUserFromSession("sessionToken123");

        // Assert
        assertNotNull(user);
        assertEquals(testUser.getId(), user.getId());
        assertEquals(testUser.getUsername(), user.getUsername());
        assertNotNull(testSession.getLastActivityAt());

        verify(sessionRepository, times(1)).findBySessionToken("sessionToken123");
        verify(sessionRepository, times(1)).save(testSession);
    }

    @Test
    void testGetUserFromSessionNotFound() {
        // Arrange
        when(sessionRepository.findBySessionToken("invalidToken"))
            .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authenticationService.getUserFromSession("invalidToken"));
        assertEquals("Invalid session", exception.getMessage());

        verify(sessionRepository, times(1)).findBySessionToken("invalidToken");
    }

    @Test
    void testGetUserFromSessionInactive() {
        // Arrange
        testSession.setActive(false);
        when(sessionRepository.findBySessionToken("sessionToken123"))
            .thenReturn(Optional.of(testSession));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authenticationService.getUserFromSession("sessionToken123"));
        assertEquals("Session is inactive", exception.getMessage());

        verify(sessionRepository, times(1)).findBySessionToken("sessionToken123");
    }

    @Test
    void testGetUserFromSessionWithLogout() {
        // Arrange
        testSession.setActive(true);
        testSession.setLogoutAt(LocalDateTime.now());
        when(sessionRepository.findBySessionToken("sessionToken123"))
            .thenReturn(Optional.of(testSession));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authenticationService.getUserFromSession("sessionToken123"));
        assertEquals("Session is inactive", exception.getMessage());

        verify(sessionRepository, times(1)).findBySessionToken("sessionToken123");
    }
}

