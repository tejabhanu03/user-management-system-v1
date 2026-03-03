-- V7__Insert_sample_data.sql
-- Migration: Insert sample data for testing

-- Insert sample users
INSERT INTO users (id, username, password, email, full_name, active) VALUES
(UUID(), 'admin', '$2a$10$slYQmyNdGzIn9KqQ7n7Dmu4kJULV5qq6voWvP/UtesSBC68IyKl5m', 'admin@example.com', 'Admin User', true),
(UUID(), 'john_doe', '$2a$10$slYQmyNdGzIn9KqQ7n7Dmu4kJULV5qq6voWvP/UtesSBC68IyKl5m', 'john@example.com', 'John Doe', true),
(UUID(), 'jane_smith', '$2a$10$slYQmyNdGzIn9KqQ7n7Dmu4kJULV5qq6voWvP/UtesSBC68IyKl5m', 'jane@example.com', 'Jane Smith', true);

-- Insert sample roles
INSERT INTO roles (id, role_name, description, active) VALUES
(UUID(), 'ADMIN', 'Administrator role with full access', true),
(UUID(), 'USER', 'Regular user role with limited access', true),
(UUID(), 'MANAGER', 'Manager role with management access', true);

-- Insert sample permissions
INSERT INTO permissions (id, permission_name, description, active) VALUES
(UUID(), 'READ', 'Permission to read data', true),
(UUID(), 'WRITE', 'Permission to write data', true),
(UUID(), 'DELETE', 'Permission to delete data', true),
(UUID(), 'MANAGE_USERS', 'Permission to manage users', true),
(UUID(), 'MANAGE_ROLES', 'Permission to manage roles', true),
(UUID(), 'VIEW_REPORTS', 'Permission to view reports', true);

-- NOTE: Assignments below need the generated UUIDs. We'll insert by selecting ids by name.

-- Assign roles to users
INSERT INTO user_roles (id, user_id, role_id, active)
SELECT UUID(), u.id, r.id, true
FROM users u JOIN roles r ON (
    (u.username = 'admin' AND r.role_name = 'ADMIN') OR
    (u.username = 'john_doe' AND r.role_name = 'USER') OR
    (u.username = 'jane_smith' AND r.role_name = 'MANAGER')
);

-- Assign permissions to roles
INSERT INTO role_permissions (id, role_id, permission_id, active)
SELECT UUID(), r.id, p.id, true
FROM roles r JOIN permissions p ON (
    (r.role_name = 'ADMIN' AND p.permission_name IN ('READ','WRITE','DELETE','MANAGE_USERS','MANAGE_ROLES','VIEW_REPORTS')) OR
    (r.role_name = 'USER' AND p.permission_name IN ('READ','VIEW_REPORTS')) OR
    (r.role_name = 'MANAGER' AND p.permission_name IN ('READ','WRITE','MANAGE_USERS','VIEW_REPORTS'))
);
