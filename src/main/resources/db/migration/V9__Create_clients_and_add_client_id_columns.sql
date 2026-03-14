-- V9__Create_clients_and_add_client_id_columns.sql
-- Migration: Add clients table and client_id to base domain tables

CREATE TABLE clients (
    id CHAR(36) PRIMARY KEY,
    client_name VARCHAR(255) NOT NULL UNIQUE,
    client_location VARCHAR(255),
    on_boarded TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,

    INDEX idx_clients_name (client_name),
    INDEX idx_clients_enabled (is_enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO clients (id, client_name, client_location, on_boarded, is_enabled)
VALUES (UUID(), 'DEFAULT_CLIENT', 'GLOBAL', NOW(), TRUE);

SET @default_client_id = (SELECT id FROM clients WHERE client_name = 'DEFAULT_CLIENT' LIMIT 1);

ALTER TABLE users
    ADD COLUMN client_id CHAR(36) NULL,
    ADD INDEX idx_users_client_id (client_id);

UPDATE users
SET client_id = @default_client_id
WHERE client_id IS NULL;

ALTER TABLE users
    MODIFY COLUMN client_id CHAR(36) NOT NULL,
    ADD CONSTRAINT fk_users_client FOREIGN KEY (client_id) REFERENCES clients(id);

ALTER TABLE roles
    ADD COLUMN client_id CHAR(36) NULL,
    ADD INDEX idx_roles_client_id (client_id);

UPDATE roles
SET client_id = @default_client_id
WHERE client_id IS NULL;

ALTER TABLE roles
    MODIFY COLUMN client_id CHAR(36) NOT NULL,
    ADD CONSTRAINT fk_roles_client FOREIGN KEY (client_id) REFERENCES clients(id);

ALTER TABLE permissions
    ADD COLUMN client_id CHAR(36) NULL,
    ADD INDEX idx_permissions_client_id (client_id);

UPDATE permissions
SET client_id = @default_client_id
WHERE client_id IS NULL;

ALTER TABLE permissions
    MODIFY COLUMN client_id CHAR(36) NOT NULL,
    ADD CONSTRAINT fk_permissions_client FOREIGN KEY (client_id) REFERENCES clients(id);

ALTER TABLE sessions
    ADD COLUMN client_id CHAR(36) NULL,
    ADD INDEX idx_sessions_client_id (client_id);

UPDATE sessions s
JOIN users u ON s.user_id = u.id
SET s.client_id = u.client_id
WHERE s.client_id IS NULL;

ALTER TABLE sessions
    MODIFY COLUMN client_id CHAR(36) NOT NULL,
    ADD CONSTRAINT fk_sessions_client FOREIGN KEY (client_id) REFERENCES clients(id);
