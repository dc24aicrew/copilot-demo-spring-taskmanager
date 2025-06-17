package com.demo.copilot.taskmanager.application.service;

import com.demo.copilot.taskmanager.application.dto.user.CreateUserRequest;
import com.demo.copilot.taskmanager.application.dto.user.UpdateUserRequest;
import com.demo.copilot.taskmanager.application.dto.user.UserResponse;
import com.demo.copilot.taskmanager.application.exception.DuplicateEmailException;
import com.demo.copilot.taskmanager.application.exception.DuplicateUsernameException;
import com.demo.copilot.taskmanager.application.exception.UserNotFoundException;
import com.demo.copilot.taskmanager.application.mapper.UserMapper;
import com.demo.copilot.taskmanager.domain.entity.User;
import com.demo.copilot.taskmanager.domain.valueobject.Email;
import com.demo.copilot.taskmanager.domain.valueobject.UserId;
import com.demo.copilot.taskmanager.infrastructure.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for user management operations.
 * 
 * This service orchestrates user-related business operations and
 * coordinates between the domain layer and infrastructure layer.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, 
                      UserMapper userMapper, 
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create a new user.
     */
    public UserResponse createUser(CreateUserRequest request) {
        // Check for duplicate email
        Email email = Email.of(request.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("User with email " + request.getEmail() + " already exists");
        }

        // Check for duplicate username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUsernameException("User with username " + request.getUsername() + " already exists");
        }

        // Hash the password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create user entity
        User user = new User.Builder()
                .id(UserId.generate())
                .username(request.getUsername())
                .email(email)
                .passwordHash(hashedPassword)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .isActive(true)
                .build();

        // Save and return response
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    /**
     * Get user by ID.
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(UserId id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        return userMapper.toResponse(user);
    }

    /**
     * Get user by email.
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String emailAddress) {
        Email email = Email.of(emailAddress);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + emailAddress));
        return userMapper.toResponse(user);
    }

    /**
     * Get user by username.
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        return userMapper.toResponse(user);
    }

    /**
     * Get all users.
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all active users.
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getActiveUsers() {
        return userRepository.findByIsActiveTrue()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update user information.
     */
    public UserResponse updateUser(UserId id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        // Update email if provided and different
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail().getValue())) {
            Email newEmail = Email.of(request.getEmail());
            if (userRepository.existsByEmail(newEmail)) {
                throw new DuplicateEmailException("User with email " + request.getEmail() + " already exists");
            }
            // Note: Email update would require updating the embedded Email value object
        }

        // Update username if provided and different
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateUsernameException("User with username " + request.getUsername() + " already exists");
            }
            // Note: Username update would require reflection or setter method
        }

        // Update profile information
        user.updateProfile(request.getFirstName(), request.getLastName(), request.getAvatarUrl());

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    /**
     * Activate a user.
     */
    public UserResponse activateUser(UserId id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        
        user.activate();
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    /**
     * Deactivate a user.
     */
    public UserResponse deactivateUser(UserId id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        
        user.deactivate();
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    /**
     * Delete a user.
     */
    public void deleteUser(UserId id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Change user password.
     */
    public void changePassword(UserId id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.changePassword(hashedPassword);
        userRepository.save(user);
    }

    /**
     * Update last login timestamp.
     */
    public void updateLastLogin(UserId id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        
        user.updateLastLogin();
        userRepository.save(user);
    }
}
