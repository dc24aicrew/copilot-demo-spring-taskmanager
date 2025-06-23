package com.demo.copilot.taskmanager.infrastructure.security;

import java.time.Duration;
import java.time.Instant;

/**
 * Interface for JWT token blacklisting services.
 * 
 * This interface abstracts the blacklisting implementation to support
 * both Redis-based and in-memory blacklisting strategies.
 */
public interface BlacklistService {
    
    /**
     * Blacklists a JWT token by its JTI.
     * 
     * @param jti JWT ID to blacklist
     * @param expirationTime When the token expires naturally
     */
    void blacklistToken(String jti, Instant expirationTime);
    
    /**
     * Checks if a JWT token is blacklisted.
     * 
     * @param jti JWT ID to check
     * @return true if the token is blacklisted, false otherwise
     */
    boolean isTokenBlacklisted(String jti);
    
    /**
     * Blacklists all tokens for a specific user.
     * 
     * @param userId User ID whose tokens should be blacklisted
     * @param blacklistDuration How long to maintain the user blacklist
     */
    void blacklistAllUserTokens(String userId, Duration blacklistDuration);
    
    /**
     * Checks if all tokens for a user are blacklisted and if the token was issued
     * before the blacklist timestamp.
     * 
     * @param userId User ID to check
     * @param tokenIssuedAt When the token was issued
     * @return true if user tokens are blacklisted and this token predates the blacklist
     */
    boolean areUserTokensBlacklisted(String userId, Instant tokenIssuedAt);
}