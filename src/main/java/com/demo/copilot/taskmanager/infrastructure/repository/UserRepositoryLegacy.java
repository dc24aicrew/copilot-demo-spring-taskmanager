package com.demo.copilot.taskmanager.infrastructure.repository;

import java.time.OffsetDateTime;
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
 * Legacy repository interface for User entity data access operations.
 * 
 * @deprecated This interface violates Clean Architecture principles.
 * Use {@link com.demo.copilot.taskmanager.domain.repository.UserRepositoryContract} instead.
 */
@Deprecated
@Repository
public interface UserRepositoryLegacy extends JpaRepository<User, UserId> {

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
    List<User> findUsersNotLoggedInSince(@Param("date") OffsetDateTime date);

    /**
     * Find users created between two dates.
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<User> findUsersCreatedBetween(@Param("startDate") OffsetDateTime startDate, 
                                      @Param("endDate") OffsetDateTime endDate);

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
