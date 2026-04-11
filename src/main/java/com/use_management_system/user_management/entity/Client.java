package com.use_management_system.user_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(name = "client_name", nullable = false, unique = true)
    private String clientName;

    @Column(name = "client_location")
    private String clientLocation;

    @Column(name = "on_boarded", nullable = false, updatable = false)
    private LocalDateTime onBoarded;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientLocation() {
        return clientLocation;
    }

    public void setClientLocation(String clientLocation) {
        this.clientLocation = clientLocation;
    }

    public LocalDateTime getOnBoarded() {
        return onBoarded;
    }

    public void setOnBoarded(LocalDateTime onBoarded) {
        this.onBoarded = onBoarded;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @PrePersist
    protected void onCreate() {
        if (onBoarded == null) {
            onBoarded = LocalDateTime.now();
        }
        if (isEnabled == null) {
            isEnabled = true;
        }
    }
}
