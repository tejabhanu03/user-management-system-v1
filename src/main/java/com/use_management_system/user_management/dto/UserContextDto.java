package com.use_management_system.user_management.dto;

import java.util.List;
import java.util.UUID;

public class UserContextDto {

    private UUID userId;
    private UUID clientId;
    private String username;
    private List<String> roles;
    private List<String> permissions;

    public UserContextDto() {
    }

    public UserContextDto(UUID userId, UUID clientId, String username, List<String> roles, List<String> permissions) {
        this.userId = userId;
        this.clientId = clientId;
        this.username = username;
        this.roles = roles;
        this.permissions = permissions;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
