package com.use_management_system.user_management.service;

import com.use_management_system.user_management.dto.VerificationResponse;
import com.use_management_system.user_management.entity.User;
import com.use_management_system.user_management.exception.VerificationRateLimitException;
import com.use_management_system.user_management.exception.VerificationTokenInvalidException;
import com.use_management_system.user_management.repository.UserRepository;
import com.use_management_system.user_management.util.EmailVerificationTokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class EmailVerificationService {

    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);
    private static final Duration RESEND_WINDOW = Duration.ofHours(24);
    private static final int MAX_RESEND_IN_WINDOW = 5;

    private final UserRepository userRepository;
    private final EmailVerificationTokenUtil emailVerificationTokenUtil;
    private final EmailSenderService emailSenderService;
    private final String verifyBaseUrl;

    public EmailVerificationService(UserRepository userRepository,
                                    EmailVerificationTokenUtil emailVerificationTokenUtil,
                                    EmailSenderService emailSenderService,
                                    @Value("${app.email.verify-base-url:http://localhost:8080/api/auth/verify-email}")
                                    String verifyBaseUrl) {
        this.userRepository = userRepository;
        this.emailVerificationTokenUtil = emailVerificationTokenUtil;
        this.emailSenderService = emailSenderService;
        this.verifyBaseUrl = verifyBaseUrl;
    }

    @Transactional
    public void sendVerificationForNewUser(User user) {
        updateResendTracking(user, LocalDateTime.now());
        userRepository.save(user);
        sendVerificationLink(user);
    }

    @Transactional
    public VerificationResponse verifyEmail(String token) {
        var parsedToken = emailVerificationTokenUtil.parseToken(token);

        User user = userRepository.findById(parsedToken.userId())
                .orElseThrow(VerificationTokenInvalidException::new);

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            return new VerificationResponse(true, "Email already verified");
        }

        if (!user.getEmail().equalsIgnoreCase(parsedToken.email())) {
            throw new VerificationTokenInvalidException();
        }

        if (parsedToken.emailVerificationVersion() != user.getEmailVerificationVersion()) {
            throw new VerificationTokenInvalidException();
        }

        user.setEmailVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        user.setActive(true);
        userRepository.save(user);

        return new VerificationResponse(true, "Email verified successfully");
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            if (Boolean.TRUE.equals(user.getEmailVerified())) {
                return;
            }

            LocalDateTime now = LocalDateTime.now();
            enforceResendRateLimit(user, now);
            user.setEmailVerificationVersion(user.getEmailVerificationVersion() + 1);
            updateResendTracking(user, now);
            userRepository.save(user);

            sendVerificationLink(user);
        });
    }

    private void enforceResendRateLimit(User user, LocalDateTime now) {
        LocalDateTime lastSentAt = user.getVerificationLastSentAt();
        if (lastSentAt != null && Duration.between(lastSentAt, now).compareTo(RESEND_COOLDOWN) < 0) {
            throw new VerificationRateLimitException("Please wait at least 60 seconds before requesting again");
        }

        LocalDateTime windowStart = user.getVerificationResendWindowStart();
        if (windowStart == null || Duration.between(windowStart, now).compareTo(RESEND_WINDOW) >= 0) {
            user.setVerificationResendWindowStart(now);
            user.setVerificationResendCount(0);
            return;
        }

        if (user.getVerificationResendCount() >= MAX_RESEND_IN_WINDOW) {
            throw new VerificationRateLimitException("Daily verification email limit reached. Try again later");
        }
    }

    private void updateResendTracking(User user, LocalDateTime now) {
        LocalDateTime windowStart = user.getVerificationResendWindowStart();
        if (windowStart == null || Duration.between(windowStart, now).compareTo(RESEND_WINDOW) >= 0) {
            user.setVerificationResendWindowStart(now);
            user.setVerificationResendCount(0);
        }

        user.setVerificationLastSentAt(now);
        user.setVerificationResendCount(user.getVerificationResendCount() + 1);
    }

    private void sendVerificationLink(User user) {
        String token = emailVerificationTokenUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getEmailVerificationVersion()
        );
        String verificationUrl = verifyBaseUrl + "?token=" + token;

        emailSenderService.sendVerificationEmail(user.getEmail(), user.getUsername(), verificationUrl);
    }
}
