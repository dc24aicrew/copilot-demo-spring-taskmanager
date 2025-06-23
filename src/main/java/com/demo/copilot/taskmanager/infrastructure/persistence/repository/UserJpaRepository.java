package com.demo.copilot.taskmanager.infrastructure.persistence.repository;

import com.demo.copilot.taskmanager.domain.valueobject.Email;
import com.demo.copilot.taskmanager.domain.valueobject.UserId;
import com.demo.copilot.taskmanager.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA repository interface for UserJpaEntity data access operations.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UserId> {

    /**
     * Find a user by their email address.
     */
    Optional<UserJpaEntity> findByEmail(Email email);

    /**
     * Find a user by their username.
     */
    Optional<UserJpaEntity> findByUsername(String username);

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
    List<UserJpaEntity> findByIsActiveTrue();

    /**
     * Find all inactive users.
     */
    List<UserJpaEntity> findByIsActiveFalse();

    /**
     * Find users by role.
     */
    @Query("SELECT u FROM UserJpaEntity u WHERE u.role = :role")
    List<UserJpaEntity> findByRole(@Param("role") String role);

    /**
     * Find users who have not logged in since a specific date.
     */
    @Query("SELECT u FROM UserJpaEntity u WHERE u.lastLoginAt < :date OR u.lastLoginAt IS NULL")
    List<UserJpaEntity> findUsersNotLoggedInSince(@Param("date") OffsetDateTime date);

    /**
     * Find users created between two dates.
     */
    @Query("SELECT u FROM UserJpaEntity u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<UserJpaEntity> findUsersCreatedBetween(@Param("startDate") OffsetDateTime startDate, 
                                                 @Param("endDate") OffsetDateTime endDate);

    /**
     * Count active users.
     */
    @Query("SELECT COUNT(u) FROM UserJpaEntity u WHERE u.isActive = true")
    long countActiveUsers();

    /**
     * Count users by role.
     */
    @Query("SELECT COUNT(u) FROM UserJpaEntity u WHERE u.role = :role")
    long countUsersByRole(@Param("role") String role);
}