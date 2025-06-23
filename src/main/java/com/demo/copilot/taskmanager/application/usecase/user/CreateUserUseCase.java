package com.demo.copilot.taskmanager.application.usecase.user;

import com.demo.copilot.taskmanager.domain.entity.User;
import com.demo.copilot.taskmanager.domain.repository.UserRepositoryContract;
import com.demo.copilot.taskmanager.domain.valueobject.Email;
import com.demo.copilot.taskmanager.domain.valueobject.UserId;
import com.demo.copilot.taskmanager.domain.valueobject.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Use case for creating a new user.
 * 
 * This class encapsulates the business logic for user creation
 * following Clean Architecture principles.
 */
@Component
@Transactional
public class CreateUserUseCase {

    private final UserRepositoryContract userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUseCase(UserRepositoryContract userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Execute the use case to create a new user.
     */
    public User execute(CreateUserCommand command) {
        Objects.requireNonNull(command, "Command cannot be null");
        
        // Business logic: Validate uniqueness
        Email email = Email.of(command.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email " + command.getEmail() + " already exists");
        }
        
        if (userRepository.existsByUsername(command.getUsername())) {
            throw new IllegalArgumentException("User with username " + command.getUsername() + " already exists");
        }

        // Business logic: Hash password and create user
        String hashedPassword = passwordEncoder.encode(command.getPassword());
        
        User user = new User.Builder()
                .id(UserId.generate())
                .username(validateAndCleanUsername(command.getUsername()))
                .email(email)
                .passwordHash(hashedPassword)
                .firstName(validateAndCleanName(command.getFirstName(), "First name"))
                .lastName(validateAndCleanName(command.getLastName(), "Last name"))
                .role(command.getRole() != null ? command.getRole() : UserRole.USER)
                .isActive(true)
                .avatarUrl(command.getAvatarUrl())
                .build();

        return userRepository.save(user);
    }

    private String validateAndCleanUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        String cleanUsername = username.trim();
        if (cleanUsername.length() < 3 || cleanUsername.length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }
        
        if (!cleanUsername.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Username can only contain letters, numbers, and underscores");
        }
        
        return cleanUsername;
    }

    private String validateAndCleanName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
        
        String cleanName = name.trim();
        if (cleanName.length() > 100) {
            throw new IllegalArgumentException(fieldName + " cannot exceed 100 characters");
        }
        
        return cleanName;
    }

    /**
     * Command object for creating a user.
     */
    public static class CreateUserCommand {
        private final String username;
        private final String email;
        private final String password;
        private final String firstName;
        private final String lastName;
        private final UserRole role;
        private final String avatarUrl;

        public CreateUserCommand(String username, String email, String password,
                               String firstName, String lastName, UserRole role, String avatarUrl) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
            this.avatarUrl = avatarUrl;
        }

        // Getters
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public UserRole getRole() { return role; }
        public String getAvatarUrl() { return avatarUrl; }
    }
}