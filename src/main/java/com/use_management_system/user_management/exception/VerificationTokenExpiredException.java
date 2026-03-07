package com.use_management_system.user_management.exception;

import org.springframework.http.HttpStatus;

public class VerificationTokenExpiredException extends ApiException {

    public VerificationTokenExpiredException() {
        super("VERIFICATION_TOKEN_EXPIRED", "Verification token has expired", HttpStatus.BAD_REQUEST);
    }
}
