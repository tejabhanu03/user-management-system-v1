package com.use_management_system.user_management.service;

import com.use_management_system.user_management.entity.User;
import com.use_management_system.user_management.exception.VerificationRateLimitException;
import com.use_management_system.user_management.repository.UserRepository;
import com.use_management_system.user_management.util.EmailVerificationTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailVerificationTokenUtil emailVerificationTokenUtil;

    @Mock
    private EmailSenderService emailSenderService;

    private EmailVerificationService service;

    @BeforeEach
    void setUp() {
        service = new EmailVerificationService(
                userRepository,
                emailVerificationTokenUtil,
                emailSenderService,
                "http://localhost:8080/api/auth/verify-email"
        );
    }

    @Test
    void resendShouldRateLimitWithinCooldown() {
        User user = baseUser();
        user.setVerificationLastSentAt(LocalDateTime.now().minusSeconds(20));
        user.setVerificationResendWindowStart(LocalDateTime.now().minusHours(1));
        user.setVerificationResendCount(1);

        when(userRepository.findByEmailIgnoreCase("john@example.com")).thenReturn(Optional.of(user));

        assertThrows(VerificationRateLimitException.class, () -> service.resendVerificationEmail("john@example.com"));
    }

    @Test
    void resendShouldRateLimitAfterDailyLimit() {
        User user = baseUser();
        user.setVerificationLastSentAt(LocalDateTime.now().minusMinutes(2));
        user.setVerificationResendWindowStart(LocalDateTime.now().minusHours(1));
        user.setVerificationResendCount(5);

        when(userRepository.findByEmailIgnoreCase("john@example.com")).thenReturn(Optional.of(user));

        assertThrows(VerificationRateLimitException.class, () -> service.resendVerificationEmail("john@example.com"));
    }

    private User baseUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setEmailVerified(false);
        user.setEmailVerificationVersion(0);
        user.setVerificationResendCount(0);
        return user;
    }
}
