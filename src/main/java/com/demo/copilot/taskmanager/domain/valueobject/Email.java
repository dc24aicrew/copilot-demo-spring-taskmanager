package com.demo.copilot.taskmanager.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Value object representing an email address.
 * 
 * This class encapsulates email validation logic and ensures
 * that only valid email addresses are used throughout the system.
 */
@Embeddable
public class Email {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    @Column(name = "email")
    private String value;

    // Default constructor for JPA
    protected Email() {}

    private Email(String value) {
        this.value = validateAndNormalize(value);
    }

    /**
     * Create an Email from a string value.
     */
    public static Email of(String value) {
        return new Email(value);
    }

    private String validateAndNormalize(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        String normalizedEmail = email.trim().toLowerCase();
        
        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        
        if (normalizedEmail.length() > 255) {
            throw new IllegalArgumentException("Email cannot exceed 255 characters");
        }
        
        return normalizedEmail;
    }

    public String getValue() {
        return value;
    }

    /**
     * Get the domain part of the email address.
     */
    public String getDomain() {
        if (value == null) {
            return null;
        }
        int atIndex = value.indexOf('@');
        return atIndex != -1 ? value.substring(atIndex + 1) : null;
    }

    /**
     * Get the local part (before @) of the email address.
     */
    public String getLocalPart() {
        if (value == null) {
            return null;
        }
        int atIndex = value.indexOf('@');
        return atIndex != -1 ? value.substring(0, atIndex) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
