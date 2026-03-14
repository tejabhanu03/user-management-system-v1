package com.use_management_system.user_management.service;

import com.use_management_system.user_management.entity.Client;
import com.use_management_system.user_management.entity.Permission;
import com.use_management_system.user_management.entity.Role;
import com.use_management_system.user_management.entity.RolePermission;
import com.use_management_system.user_management.repository.PermissionRepository;
import com.use_management_system.user_management.repository.RolePermissionRepository;
import com.use_management_system.user_management.repository.RoleRepository;
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
class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private PermissionService permissionService;

    private UUID permissionId;
    private UUID roleId;
    private UUID clientId;
    private Permission testPermission;
    private Role testRole;
    private Client testClient;

    @BeforeEach
    void setUp() {
        permissionId = UUID.randomUUID();
        roleId = UUID.randomUUID();
        clientId = UUID.randomUUID();

        testClient = new Client();
        testClient.setId(clientId);
        testClient.setClientName("DEFAULT_CLIENT");
        testClient.setIsEnabled(true);

        testPermission = new Permission();
        testPermission.setId(permissionId);
        testPermission.setPermissionName("VIEW_USERS");
        testPermission.setDescription("Permission to view users");
        testPermission.setClient(testClient);
        testPermission.setActive(true);

        testRole = new Role();
        testRole.setId(roleId);
        testRole.setRoleName("ADMIN");
        testRole.setDescription("Administrator role");
        testRole.setClient(testClient);
        testRole.setActive(true);
    }

    @Test
    void testCreatePermissionSuccess() {
        // Arrange
        when(permissionRepository.findByPermissionName("VIEW_USERS")).thenReturn(Optional.empty());
        when(clientService.resolveClient(null)).thenReturn(testClient);
        when(permissionRepository.save(any(Permission.class))).thenReturn(testPermission);

        // Act
        Permission createdPermission = permissionService.createPermission("VIEW_USERS", "Permission to view users", null);

        // Assert
        assertNotNull(createdPermission);
        assertEquals("VIEW_USERS", createdPermission.getPermissionName());
        assertEquals("Permission to view users", createdPermission.getDescription());
        assertTrue(createdPermission.getActive());

        verify(permissionRepository, times(1)).findByPermissionName("VIEW_USERS");
        verify(clientService, times(1)).resolveClient(null);
        verify(permissionRepository, times(1)).save(any(Permission.class));
    }

    @Test
    void testCreatePermissionWithExistingName() {
        // Arrange
        when(permissionRepository.findByPermissionName("VIEW_USERS")).thenReturn(Optional.of(testPermission));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> permissionService.createPermission("VIEW_USERS", "Permission to view users", null));
        assertEquals("Permission already exists", exception.getMessage());

        verify(permissionRepository, times(1)).findByPermissionName("VIEW_USERS");
        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    void testGetPermissionByIdSuccess() {
        // Arrange
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(testPermission));

        // Act
        Permission permission = permissionService.getPermissionById(permissionId);

        // Assert
        assertNotNull(permission);
        assertEquals(permissionId, permission.getId());
        assertEquals("VIEW_USERS", permission.getPermissionName());

        verify(permissionRepository, times(1)).findById(permissionId);
    }

    @Test
    void testGetPermissionByIdNotFound() {
        // Arrange
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> permissionService.getPermissionById(permissionId));
        assertEquals("Permission not found", exception.getMessage());

        verify(permissionRepository, times(1)).findById(permissionId);
    }

    @Test
    void testGetPermissionByNameSuccess() {
        // Arrange
        when(permissionRepository.findByPermissionName("VIEW_USERS")).thenReturn(Optional.of(testPermission));

        // Act
        Permission permission = permissionService.getPermissionByName("VIEW_USERS");

        // Assert
        assertNotNull(permission);
        assertEquals("VIEW_USERS", permission.getPermissionName());

        verify(permissionRepository, times(1)).findByPermissionName("VIEW_USERS");
    }

    @Test
    void testGetPermissionByNameNotFound() {
        // Arrange
        when(permissionRepository.findByPermissionName("UNKNOWN")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> permissionService.getPermissionByName("UNKNOWN"));
        assertEquals("Permission not found", exception.getMessage());

        verify(permissionRepository, times(1)).findByPermissionName("UNKNOWN");
    }

    @Test
    void testGetAllPermissionsSuccess() {
        // Arrange
        Permission permission1 = new Permission();
        permission1.setId(UUID.randomUUID());
        permission1.setPermissionName("VIEW_USERS");

        Permission permission2 = new Permission();
        permission2.setId(UUID.randomUUID());
        permission2.setPermissionName("CREATE_USERS");

        List<Permission> permissions = Arrays.asList(permission1, permission2);
        when(permissionRepository.findAll()).thenReturn(permissions);

        // Act
        List<Permission> retrievedPermissions = permissionService.getAllPermissions();

        // Assert
        assertNotNull(retrievedPermissions);
        assertEquals(2, retrievedPermissions.size());

        verify(permissionRepository, times(1)).findAll();
    }

    @Test
    void testGetAllPermissionsEmpty() {
        // Arrange
        when(permissionRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Permission> permissions = permissionService.getAllPermissions();

        // Assert
        assertNotNull(permissions);
        assertEquals(0, permissions.size());

        verify(permissionRepository, times(1)).findAll();
    }

    @Test
    void testAssignPermissionToRoleSuccess() {
        // Arrange
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(testRole));
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(testPermission));
        when(rolePermissionRepository.save(any(RolePermission.class))).thenReturn(new RolePermission());

        // Act
        permissionService.assignPermissionToRole(roleId, permissionId);

        // Assert
        verify(roleRepository, times(1)).findById(roleId);
        verify(permissionRepository, times(1)).findById(permissionId);
        verify(rolePermissionRepository, times(1)).save(any(RolePermission.class));
    }

    @Test
    void testAssignPermissionToRoleNotFoundRole() {
        // Arrange
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> permissionService.assignPermissionToRole(roleId, permissionId));
        assertEquals("Role not found", exception.getMessage());

        verify(roleRepository, times(1)).findById(roleId);
        verify(permissionRepository, never()).findById(any());
        verify(rolePermissionRepository, never()).save(any(RolePermission.class));
    }

    @Test
    void testAssignPermissionToRoleNotFoundPermission() {
        // Arrange
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(testRole));
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> permissionService.assignPermissionToRole(roleId, permissionId));
        assertEquals("Permission not found", exception.getMessage());

        verify(roleRepository, times(1)).findById(roleId);
        verify(permissionRepository, times(1)).findById(permissionId);
        verify(rolePermissionRepository, never()).save(any(RolePermission.class));
    }

    @Test
    void testRemovePermissionFromRoleSuccess() {
        // Arrange
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(testRole);
        rolePermission.setPermission(testPermission);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(testRole));
        when(rolePermissionRepository.findByRole(testRole)).thenReturn(Arrays.asList(rolePermission));

        // Act
        permissionService.removePermissionFromRole(roleId, permissionId);

        // Assert
        verify(roleRepository, times(1)).findById(roleId);
        verify(rolePermissionRepository, times(1)).findByRole(testRole);
        verify(rolePermissionRepository, times(1)).delete(rolePermission);
    }

    @Test
    void testRemovePermissionFromRoleNotFound() {
        // Arrange
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> permissionService.removePermissionFromRole(roleId, permissionId));
        assertEquals("Role not found", exception.getMessage());

        verify(roleRepository, times(1)).findById(roleId);
        verify(rolePermissionRepository, never()).delete(any(RolePermission.class));
    }

    @Test
    void testRemovePermissionFromRoleMappingNotFound() {
        // Arrange
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(testRole));
        when(rolePermissionRepository.findByRole(testRole)).thenReturn(Collections.emptyList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> permissionService.removePermissionFromRole(roleId, permissionId));
        assertEquals("Role permission mapping not found", exception.getMessage());

        verify(roleRepository, times(1)).findById(roleId);
        verify(rolePermissionRepository, times(1)).findByRole(testRole);
        verify(rolePermissionRepository, never()).delete(any(RolePermission.class));
    }

    @Test
    void testGetRolePermissionsSuccess() {
        // Arrange
        RolePermission rolePermission1 = new RolePermission();
        Permission permission1 = new Permission();
        permission1.setId(UUID.randomUUID());
        permission1.setPermissionName("VIEW_USERS");
        rolePermission1.setPermission(permission1);

        RolePermission rolePermission2 = new RolePermission();
        Permission permission2 = new Permission();
        permission2.setId(UUID.randomUUID());
        permission2.setPermissionName("CREATE_USERS");
        rolePermission2.setPermission(permission2);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(testRole));
        when(rolePermissionRepository.findByRoleAndActive(testRole, true))
            .thenReturn(Arrays.asList(rolePermission1, rolePermission2));

        // Act
        List<Permission> permissions = permissionService.getRolePermissions(roleId);

        // Assert
        assertNotNull(permissions);
        assertEquals(2, permissions.size());

        verify(roleRepository, times(1)).findById(roleId);
        verify(rolePermissionRepository, times(1)).findByRoleAndActive(testRole, true);
    }

    @Test
    void testGetRolePermissionsEmpty() {
        // Arrange
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(testRole));
        when(rolePermissionRepository.findByRoleAndActive(testRole, true))
            .thenReturn(Collections.emptyList());

        // Act
        List<Permission> permissions = permissionService.getRolePermissions(roleId);

        // Assert
        assertNotNull(permissions);
        assertEquals(0, permissions.size());

        verify(roleRepository, times(1)).findById(roleId);
        verify(rolePermissionRepository, times(1)).findByRoleAndActive(testRole, true);
    }

    @Test
    void testGetRolePermissionsRoleNotFound() {
        // Arrange
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> permissionService.getRolePermissions(roleId));
        assertEquals("Role not found", exception.getMessage());

        verify(roleRepository, times(1)).findById(roleId);
        verify(rolePermissionRepository, never()).findByRoleAndActive(any(), any());
    }
}
