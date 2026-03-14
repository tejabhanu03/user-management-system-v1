package com.use_management_system.user_management.service;

import com.use_management_system.user_management.entity.Client;
import com.use_management_system.user_management.entity.Role;
import com.use_management_system.user_management.entity.User;
import com.use_management_system.user_management.entity.UserRole;
import com.use_management_system.user_management.repository.RoleRepository;
import com.use_management_system.user_management.repository.UserRepository;
import com.use_management_system.user_management.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private RoleService roleService;

    private UUID roleId;
    private UUID userId;
    private UUID clientId;
    private Role testRole;
    private User testUser;
    private Client testClient;

    @BeforeEach
    void setUp() {
        roleId = UUID.randomUUID();
        userId = UUID.randomUUID();
        clientId = UUID.randomUUID();

        testClient = new Client();
        testClient.setId(clientId);
        testClient.setClientName("DEFAULT_CLIENT");
        testClient.setIsEnabled(true);

        testRole = new Role();
        testRole.setId(roleId);
        testRole.setRoleName("ADMIN");
        testRole.setDescription("Administrator role");
        testRole.setClient(testClient);
        testRole.setActive(true);

        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setClient(testClient);
        testUser.setActive(true);
    }

    @Test
    void testCreateRoleSuccess() {
        // Arrange
        when(roleRepository.findByRoleName("ADMIN")).thenReturn(Optional.empty());
        when(clientService.resolveClient(null)).thenReturn(testClient);
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);

        // Act
        Role createdRole = roleService.createRole("ADMIN", "Administrator role", null);

        // Assert
        assertNotNull(createdRole);
        assertEquals("ADMIN", createdRole.getRoleName());
        assertEquals("Administrator role", createdRole.getDescription());
        assertTrue(createdRole.getActive());

        verify(roleRepository, times(1)).findByRoleName("ADMIN");
        verify(clientService, times(1)).resolveClient(null);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void testCreateRoleWithExistingName() {
        // Arrange
        when(roleRepository.findByRoleName("ADMIN")).thenReturn(Optional.of(testRole));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> roleService.createRole("ADMIN", "Administrator role", null));
        assertEquals("Role already exists", exception.getMessage());

        verify(roleRepository, times(1)).findByRoleName("ADMIN");
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testGetRoleByIdSuccess() {
        // Arrange
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(testRole));

        // Act
        Role role = roleService.getRoleById(roleId);

        // Assert
        assertNotNull(role);
        assertEquals(roleId, role.getId());
        assertEquals("ADMIN", role.getRoleName());

        verify(roleRepository, times(1)).findById(roleId);
    }

    @Test
    void testGetRoleByIdNotFound() {
        // Arrange
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> roleService.getRoleById(roleId));
        assertEquals("Role not found", exception.getMessage());

        verify(roleRepository, times(1)).findById(roleId);
    }

    @Test
    void testGetRoleByNameSuccess() {
        // Arrange
        when(roleRepository.findByRoleName("ADMIN")).thenReturn(Optional.of(testRole));

        // Act
        Role role = roleService.getRoleByName("ADMIN");

        // Assert
        assertNotNull(role);
        assertEquals("ADMIN", role.getRoleName());

        verify(roleRepository, times(1)).findByRoleName("ADMIN");
    }

    @Test
    void testGetRoleByNameNotFound() {
        // Arrange
        when(roleRepository.findByRoleName("UNKNOWN")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> roleService.getRoleByName("UNKNOWN"));
        assertEquals("Role not found", exception.getMessage());

        verify(roleRepository, times(1)).findByRoleName("UNKNOWN");
    }

    @Test
    void testGetAllRolesSuccess() {
        // Arrange
        Role role1 = new Role();
        role1.setId(UUID.randomUUID());
        role1.setRoleName("ADMIN");

        Role role2 = new Role();
        role2.setId(UUID.randomUUID());
        role2.setRoleName("USER");

        List<Role> roles = Arrays.asList(role1, role2);
        when(roleRepository.findAll()).thenReturn(roles);

        // Act
        List<Role> retrievedRoles = roleService.getAllRoles();

        // Assert
        assertNotNull(retrievedRoles);
        assertEquals(2, retrievedRoles.size());

        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void testGetAllRolesEmpty() {
        // Arrange
        when(roleRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Role> roles = roleService.getAllRoles();

        // Assert
        assertNotNull(roles);
        assertEquals(0, roles.size());

        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void testAssignRoleToUserSuccess() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(testRole));
        when(userRoleRepository.save(any(UserRole.class))).thenReturn(new UserRole());

        // Act
        roleService.assignRoleToUser(userId, roleId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(roleRepository, times(1)).findById(roleId);
        verify(userRoleRepository, times(1)).save(any(UserRole.class));
    }

    @Test
    void testAssignRoleToUserNotFoundUser() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> roleService.assignRoleToUser(userId, roleId));
        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(roleRepository, never()).findById(any());
        verify(userRoleRepository, never()).save(any(UserRole.class));
    }

    @Test
    void testAssignRoleToUserNotFoundRole() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> roleService.assignRoleToUser(userId, roleId));
        assertEquals("Role not found", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(roleRepository, times(1)).findById(roleId);
        verify(userRoleRepository, never()).save(any(UserRole.class));
    }

    @Test
    void testRemoveRoleFromUserSuccess() {
        // Arrange
        UserRole userRole = new UserRole();
        userRole.setUser(testUser);
        userRole.setRole(testRole);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRoleRepository.findByUser(testUser)).thenReturn(Arrays.asList(userRole));

        // Act
        roleService.removeRoleFromUser(userId, roleId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRoleRepository, times(1)).findByUser(testUser);
        verify(userRoleRepository, times(1)).delete(userRole);
    }

    @Test
    void testRemoveRoleFromUserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> roleService.removeRoleFromUser(userId, roleId));
        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(userRoleRepository, never()).delete(any(UserRole.class));
    }

    @Test
    void testRemoveRoleFromUserMappingNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRoleRepository.findByUser(testUser)).thenReturn(Collections.emptyList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> roleService.removeRoleFromUser(userId, roleId));
        assertEquals("User role mapping not found", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(userRoleRepository, times(1)).findByUser(testUser);
        verify(userRoleRepository, never()).delete(any(UserRole.class));
    }

    @Test
    void testGetUserRolesSuccess() {
        // Arrange
        UserRole userRole1 = new UserRole();
        Role role1 = new Role();
        role1.setId(UUID.randomUUID());
        role1.setRoleName("ADMIN");
        userRole1.setRole(role1);

        UserRole userRole2 = new UserRole();
        Role role2 = new Role();
        role2.setId(UUID.randomUUID());
        role2.setRoleName("USER");
        userRole2.setRole(role2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRoleRepository.findByUserAndActive(testUser, true))
            .thenReturn(Arrays.asList(userRole1, userRole2));

        // Act
        List<Role> roles = roleService.getUserRoles(userId);

        // Assert
        assertNotNull(roles);
        assertEquals(2, roles.size());

        verify(userRepository, times(1)).findById(userId);
        verify(userRoleRepository, times(1)).findByUserAndActive(testUser, true);
    }

    @Test
    void testGetUserRolesEmpty() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRoleRepository.findByUserAndActive(testUser, true))
            .thenReturn(Collections.emptyList());

        // Act
        List<Role> roles = roleService.getUserRoles(userId);

        // Assert
        assertNotNull(roles);
        assertEquals(0, roles.size());

        verify(userRepository, times(1)).findById(userId);
        verify(userRoleRepository, times(1)).findByUserAndActive(testUser, true);
    }

    @Test
    void testGetUserRolesUserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> roleService.getUserRoles(userId));
        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(userRoleRepository, never()).findByUserAndActive(any(), any());
    }
}
