-- V8__Add_email_verification_columns.sql
-- Migration: Add email verification support for user registration flow

ALTER TABLE users
    ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN email_verified_at TIMESTAMP NULL,
    ADD COLUMN email_verification_version INT NOT NULL DEFAULT 0,
    ADD COLUMN verification_last_sent_at TIMESTAMP NULL,
    ADD COLUMN verification_resend_window_start TIMESTAMP NULL,
    ADD COLUMN verification_resend_count INT NOT NULL DEFAULT 0;

CREATE INDEX idx_users_email_verified ON users (email_verified);
CREATE INDEX idx_users_verification_last_sent_at ON users (verification_last_sent_at);

-- Existing active users are assumed to be trusted historical users.
UPDATE users
SET email_verified = TRUE,
    email_verified_at = NOW()
WHERE active = TRUE;
