package com.demo.copilot.taskmanager.domain.repository;

import com.demo.copilot.taskmanager.domain.entity.User;
import com.demo.copilot.taskmanager.domain.valueobject.Email;
import com.demo.copilot.taskmanager.domain.valueobject.UserId;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository contract for User entity.
 * 
 * This interface defines the persistence operations required by the domain layer
 * without depending on any infrastructure framework.
 */
public interface UserRepositoryContract {

    /**
     * Save a user entity.
     */
    User save(User user);

    /**
     * Find a user by ID.
     */
    Optional<User> findById(UserId id);

    /**
     * Find a user by their email address.
     */
    Optional<User> findByEmail(Email email);

    /**
     * Find a user by their username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if a user exists with the given email.
     */
    boolean existsByEmail(Email email);

    /**
     * Check if a user exists with the given username.
     */
    boolean existsByUsername(String username);

    /**
     * Find all active users.
     */
    List<User> findByIsActiveTrue();

    /**
     * Find all inactive users.
     */
    List<User> findByIsActiveFalse();

    /**
     * Find users by role.
     */
    List<User> findByRole(String role);

    /**
     * Find users who have not logged in since a specific date.
     */
    List<User> findUsersNotLoggedInSince(OffsetDateTime date);

    /**
     * Find users created between two dates.
     */
    List<User> findUsersCreatedBetween(OffsetDateTime startDate, OffsetDateTime endDate);

    /**
     * Count active users.
     */
    long countActiveUsers();

    /**
     * Count users by role.
     */
    long countUsersByRole(String role);

    /**
     * Delete a user by ID.
     */
    void deleteById(UserId id);

    /**
     * Find all users.
     */
    List<User> findAll();

    /**
     * Check if user exists by ID.
     */
    boolean existsById(UserId id);
}