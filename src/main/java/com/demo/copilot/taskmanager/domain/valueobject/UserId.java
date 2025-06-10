package com.demo.copilot.taskmanager.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Value object representing a User identifier.
 * 
 * This class encapsulates the user ID and provides type safety
 * and validation for user identification throughout the system.
 */
@Embeddable
public class UserId {

    @Column(name = "id")
    private UUID value;

    // Default constructor for JPA
    protected UserId() {}

    private UserId(UUID value) {
        this.value = Objects.requireNonNull(value, "User ID value cannot be null");
    }

    /**
     * Generate a new random User ID.
     */
    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    /**
     * Create a User ID from a UUID value.
     */
    public static UserId of(UUID value) {
        return new UserId(value);
    }

    /**
     * Create a User ID from a string representation.
     */
    public static UserId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID string cannot be null or empty");
        }
        try {
            return new UserId(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid User ID format: " + value, e);
        }
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "null";
    }
}
