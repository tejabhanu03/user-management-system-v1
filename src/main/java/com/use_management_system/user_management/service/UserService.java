package com.use_management_system.user_management.service;

import com.use_management_system.user_management.dto.RegistrationResponse;
import com.use_management_system.user_management.dto.UserRegistrationRequest;
import com.use_management_system.user_management.dto.UserResponse;
import com.use_management_system.user_management.entity.Client;
import com.use_management_system.user_management.entity.User;
import com.use_management_system.user_management.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final ClientService clientService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EmailVerificationService emailVerificationService,
                       ClientService clientService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationService = emailVerificationService;
        this.clientService = clientService;
    }

    public RegistrationResponse registerUser(UserRegistrationRequest request) {
        validateRegistrationRequest(request);

        String normalizedUsername = request.getUsername().trim();
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        String normalizedFullName = request.getFullName().trim();

        if (userRepository.findByUsername(normalizedUsername).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        Client client = clientService.resolveClient(request.getClientId());
        user.setUsername(normalizedUsername);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(normalizedEmail);
        user.setFullName(normalizedFullName);
        user.setClient(client);
        user.setActive(false);
        user.setEmailVerified(false);
        user.setEmailVerifiedAt(null);
        user.setEmailVerificationVersion(0);
        user.setVerificationLastSentAt(null);
        user.setVerificationResendWindowStart(null);
        user.setVerificationResendCount(0);

        User savedUser = userRepository.save(user);
        emailVerificationService.sendVerificationForNewUser(savedUser);

        return new RegistrationResponse(
                savedUser.getId(),
                savedUser.getClient() == null ? null : savedUser.getClient().getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                "Registration successful. Please verify your email."
        );
    }

    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse updateUser(UUID userId, UserRegistrationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null && !user.getEmail().equalsIgnoreCase(request.getEmail())) {
            String normalizedEmail = request.getEmail().trim().toLowerCase();
            if (userRepository.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(normalizedEmail);
        }

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    public void deactivateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(false);
        userRepository.save(user);
    }

    public void activateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(true);
        userRepository.save(user);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getClient() == null ? null : user.getClient().getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getActive(),
                user.getEmailVerified(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private void validateRegistrationRequest(UserRegistrationRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters");
        }
        if (request.getEmail() == null || !request.getEmail().contains("@")) {
            throw new RuntimeException("Valid email is required");
        }
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new RuntimeException("Full name is required");
        }
    }
}
