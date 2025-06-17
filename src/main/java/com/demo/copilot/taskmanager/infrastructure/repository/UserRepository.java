package com.demo.copilot.taskmanager.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.demo.copilot.taskmanager.domain.entity.User;
import com.demo.copilot.taskmanager.domain.valueobject.Email;
import com.demo.copilot.taskmanager.domain.valueobject.UserId;

/**
 * Repository interface for User entity data access operations.
 * 
 * Extends JpaRepository to provide CRUD operations and custom queries
 * for user management functionality.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UserId> {

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
    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findByRole(@Param("role") String role);

    /**
     * Find users who have not logged in since a specific date.
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :date OR u.lastLoginAt IS NULL")
    List<User> findUsersNotLoggedInSince(@Param("date") LocalDateTime date);

    /**
     * Find users created between two dates.
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<User> findUsersCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);

    /**
     * Count active users.
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();

    /**
     * Count users by role.
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countUsersByRole(@Param("role") String role);
}
