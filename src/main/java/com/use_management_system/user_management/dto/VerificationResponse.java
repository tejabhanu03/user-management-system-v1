package com.use_management_system.user_management.dto;

public class VerificationResponse {

    private boolean verified;
    private String message;

    public VerificationResponse() {
    }

    public VerificationResponse(boolean verified, String message) {
        this.verified = verified;
        this.message = message;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
