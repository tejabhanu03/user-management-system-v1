package com.use_management_system.user_management.exception;

import org.springframework.http.HttpStatus;

public class EmailVerificationRequiredException extends ApiException {

    public EmailVerificationRequiredException() {
        super("EMAIL_VERIFICATION_REQUIRED", "Email not verified", HttpStatus.UNAUTHORIZED);
    }
}
