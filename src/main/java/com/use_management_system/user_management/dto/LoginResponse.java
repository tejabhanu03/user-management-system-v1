package com.use_management_system.user_management.dto;

import java.util.List;
import java.util.UUID;

public class LoginResponse {

    private UUID userId;
    private String username;
    private String email;
    private String sessionToken;
    private String jwtToken;
    private List<String> roles;
    private List<String> permissions;
    private String message;

    public LoginResponse() {
    }

    public LoginResponse(UUID userId,
                         String username,
                         String email,
                         String sessionToken,
                         String jwtToken,
                         List<String> roles,
                         List<String> permissions,
                         String message) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.sessionToken = sessionToken;
        this.jwtToken = jwtToken;
        this.roles = roles;
        this.permissions = permissions;
        this.message = message;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
