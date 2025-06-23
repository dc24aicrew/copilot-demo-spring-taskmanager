package com.demo.copilot.taskmanager.presentation.dto.response;

import com.demo.copilot.taskmanager.application.dto.user.UserResponse;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

/**
 * Data Transfer Object for authentication responses.
 * Enhanced to support refresh tokens and comprehensive token information.
 */
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn; // Access token expiration in seconds
    private UserResponse user;
    private String message;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime timestamp;

    // Default constructor
    public AuthResponse() {
        this.timestamp = OffsetDateTime.now();
    }

    // Constructor with access token only (backward compatibility)
    public AuthResponse(String accessToken, UserResponse user, String message) {
        this.accessToken = accessToken;
        this.user = user;
        this.message = message;
        this.timestamp = OffsetDateTime.now();
    }

    // Full constructor with refresh token support
    public AuthResponse(String accessToken, String refreshToken, Long expiresIn, 
                       UserResponse user, String message) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.user = user;
        this.message = message;
        this.timestamp = OffsetDateTime.now();
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String accessToken;
        private String refreshToken;
        private Long expiresIn;
        private UserResponse user;
        private String message;

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder expiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public Builder user(UserResponse user) {
            this.user = user;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        // Legacy method for backward compatibility
        public Builder token(String token) {
            this.accessToken = token;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(accessToken, refreshToken, expiresIn, user, message);
        }
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // Legacy getter for backward compatibility
    public String getToken() {
        return accessToken;
    }

    // Legacy setter for backward compatibility
    public void setToken(String token) {
        this.accessToken = token;
    }
}