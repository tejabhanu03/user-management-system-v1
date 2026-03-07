package com.use_management_system.user_management.exception;

import org.springframework.http.HttpStatus;

public class VerificationRateLimitException extends ApiException {

    public VerificationRateLimitException(String message) {
        super("VERIFICATION_RATE_LIMITED", message, HttpStatus.TOO_MANY_REQUESTS);
    }
}
