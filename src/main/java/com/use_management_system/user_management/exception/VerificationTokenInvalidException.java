package com.use_management_system.user_management.exception;

import org.springframework.http.HttpStatus;

public class VerificationTokenInvalidException extends ApiException {

    public VerificationTokenInvalidException() {
        super("VERIFICATION_TOKEN_INVALID", "Verification token is invalid", HttpStatus.BAD_REQUEST);
    }
}
