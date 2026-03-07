package com.use_management_system.user_management.service;

import com.use_management_system.user_management.dto.RegistrationResponse;
import com.use_management_system.user_management.dto.UserRegistrationRequest;
import com.use_management_system.user_management.dto.UserResponse;
import com.use_management_system.user_management.entity.User;
import com.use_management_system.user_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User testUser;
    private UserRegistrationRequest registrationRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword123");
        testUser.setFullName("Test User");
        testUser.setActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        registrationRequest = new UserRegistrationRequest();
        registrationRequest.setUsername("testuser");
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setFullName("Test User");
    }

    @Test
    void testRegisterUserSuccess() {
        // Arrange
        when(userRepository.findByUsername(registrationRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmailIgnoreCase(registrationRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        RegistrationResponse response = userService.registerUser(registrationRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testUser.getId(), response.getUserId());
        assertEquals(testUser.getUsername(), response.getUsername());
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals("Registration successful. Please verify your email.", response.getMessage());

        // Verify interactions
        verify(userRepository, times(1)).findByUsername(registrationRequest.getUsername());
        verify(userRepository, times(1)).findByEmailIgnoreCase(registrationRequest.getEmail());
        verify(passwordEncoder, times(1)).encode(registrationRequest.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailVerificationService, times(1)).sendVerificationForNewUser(any(User.class));
    }

    @Test
    void testRegisterUserWithExistingUsername() {
        // Arrange
        when(userRepository.findByUsername(registrationRequest.getUsername())).thenReturn(Optional.of(testUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.registerUser(registrationRequest));
        assertEquals("Username already exists", exception.getMessage());

        // Verify interactions
        verify(userRepository, times(1)).findByUsername(registrationRequest.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUserWithExistingEmail() {
        // Arrange
        when(userRepository.findByUsername(registrationRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmailIgnoreCase(registrationRequest.getEmail())).thenReturn(Optional.of(testUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.registerUser(registrationRequest));
        assertEquals("Email already exists", exception.getMessage());

        // Verify interactions
        verify(userRepository, times(1)).findByUsername(registrationRequest.getUsername());
        verify(userRepository, times(1)).findByEmailIgnoreCase(registrationRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserByIdSuccess() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        UserResponse response = userService.getUserById(userId);

        // Assert
        assertNotNull(response);
        assertEquals(testUser.getId(), response.getId());
        assertEquals(testUser.getUsername(), response.getUsername());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserByIdNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.getUserById(userId));
        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserByUsernameSuccess() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserResponse response = userService.getUserByUsername("testuser");

        // Assert
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());

        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testGetUserByUsernameNotFound() {
        // Arrange
        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.getUserByUsername("unknownuser"));
        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).findByUsername("unknownuser");
    }

    @Test
    void testGetAllUsersSuccess() {
        // Arrange
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");

        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserResponse> responses = userService.getAllUsers();

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("user1", responses.get(0).getUsername());
        assertEquals("user2", responses.get(1).getUsername());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllUsersEmpty() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<UserResponse> responses = userService.getAllUsers();

        // Assert
        assertNotNull(responses);
        assertEquals(0, responses.size());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testUpdateUserSuccess() {
        // Arrange
        UserRegistrationRequest updateRequest = new UserRegistrationRequest();
        updateRequest.setFullName("Updated Name");
        updateRequest.setEmail("updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmailIgnoreCase("updated@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserResponse response = userService.updateUser(userId, updateRequest);

        // Assert
        assertNotNull(response);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByEmailIgnoreCase("updated@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.updateUser(userId, registrationRequest));
        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserWithDuplicateEmail() {
        // Arrange
        UserRegistrationRequest updateRequest = new UserRegistrationRequest();
        updateRequest.setEmail("existing@example.com");

        User existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setEmail("existing@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmailIgnoreCase("existing@example.com")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.updateUser(userId, updateRequest));
        assertEquals("Email already exists", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByEmailIgnoreCase("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeactivateUserSuccess() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.deactivateUser(userId);

        // Assert
        assertFalse(testUser.getActive());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testDeactivateUserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.deactivateUser(userId));
        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testActivateUserSuccess() {
        // Arrange
        testUser.setActive(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.activateUser(userId);

        // Assert
        assertTrue(testUser.getActive());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testActivateUserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.activateUser(userId));
        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }
}
