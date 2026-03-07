package com.use_management_system.user_management.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    @Column(name = "email_verification_version", nullable = false)
    private Integer emailVerificationVersion = 0;

    @Column(name = "verification_last_sent_at")
    private LocalDateTime verificationLastSentAt;

    @Column(name = "verification_resend_window_start")
    private LocalDateTime verificationResendWindowStart;

    @Column(name = "verification_resend_count", nullable = false)
    private Integer verificationResendCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public User() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public LocalDateTime getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(LocalDateTime emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public Integer getEmailVerificationVersion() {
        return emailVerificationVersion;
    }

    public void setEmailVerificationVersion(Integer emailVerificationVersion) {
        this.emailVerificationVersion = emailVerificationVersion;
    }

    public LocalDateTime getVerificationLastSentAt() {
        return verificationLastSentAt;
    }

    public void setVerificationLastSentAt(LocalDateTime verificationLastSentAt) {
        this.verificationLastSentAt = verificationLastSentAt;
    }

    public LocalDateTime getVerificationResendWindowStart() {
        return verificationResendWindowStart;
    }

    public void setVerificationResendWindowStart(LocalDateTime verificationResendWindowStart) {
        this.verificationResendWindowStart = verificationResendWindowStart;
    }

    public Integer getVerificationResendCount() {
        return verificationResendCount;
    }

    public void setVerificationResendCount(Integer verificationResendCount) {
        this.verificationResendCount = verificationResendCount;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
