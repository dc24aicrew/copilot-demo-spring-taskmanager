package com.demo.copilot.taskmanager.domain.entity;

import com.demo.copilot.taskmanager.domain.valueobject.Email;
import com.demo.copilot.taskmanager.domain.valueobject.UserId;
import com.demo.copilot.taskmanager.domain.valueobject.UserRole;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * User domain entity representing a system user.
 * 
 * Pure domain entity following Clean Architecture principles.
 * Contains business logic related to user management
 * without any infrastructure dependencies.
 */
public class User {

    private UserId id;
    private String username;
    private Email email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private UserRole role;
    private Boolean isActive;
    private OffsetDateTime lastLoginAt;
    private String avatarUrl;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Long version;

    // Default constructor
    protected User() {}

    private User(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.email = builder.email;
        this.passwordHash = builder.passwordHash;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.role = builder.role;
        this.isActive = builder.isActive;
        this.avatarUrl = builder.avatarUrl;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        this.version = 0L;
    }

    // Business methods
    public void activate() {
        this.isActive = true;
        this.updatedAt = OffsetDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = OffsetDateTime.now();
    }

    public void updateLastLogin() {
        this.lastLoginAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    public void changePassword(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Password hash cannot be null or empty");
        }
        this.passwordHash = newPasswordHash;
        this.updatedAt = OffsetDateTime.now();
    }

    public void updateProfile(String firstName, String lastName, String avatarUrl) {
        if (firstName != null && !firstName.trim().isEmpty()) {
            this.firstName = firstName.trim();
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            this.lastName = lastName.trim();
        }
        this.avatarUrl = avatarUrl;
        this.updatedAt = OffsetDateTime.now();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isAdmin() {
        return UserRole.ADMIN.equals(this.role);
    }

    public boolean isManager() {
        return UserRole.MANAGER.equals(this.role);
    }

    // Getters
    public UserId getId() { return id; }
    public String getUsername() { return username; }
    public Email getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public UserRole getRole() { return role; }
    public Boolean getIsActive() { return isActive; }
    public OffsetDateTime getLastLoginAt() { return lastLoginAt; }
    public String getAvatarUrl() { return avatarUrl; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", email=" + email +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", role=" + role +
               ", isActive=" + isActive +
               '}';
    }

    // Builder pattern
    public static class Builder {
        private UserId id;
        private String username;
        private Email email;
        private String passwordHash;
        private String firstName;
        private String lastName;
        private UserRole role = UserRole.USER;
        private Boolean isActive = true;
        private String avatarUrl;

        public Builder id(UserId id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(Email email) {
            this.email = email;
            return this;
        }

        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder role(UserRole role) {
            this.role = role;
            return this;
        }

        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public User build() {
            Objects.requireNonNull(id, "User ID cannot be null");
            Objects.requireNonNull(username, "Username cannot be null");
            Objects.requireNonNull(email, "Email cannot be null");
            Objects.requireNonNull(passwordHash, "Password hash cannot be null");
            Objects.requireNonNull(firstName, "First name cannot be null");
            Objects.requireNonNull(lastName, "Last name cannot be null");
            
            return new User(this);
        }
    }
}