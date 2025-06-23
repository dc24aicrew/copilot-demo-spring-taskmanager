package com.demo.copilot.taskmanager.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * In-memory fallback JWT blacklisting service for when Redis is not available.
 * 
 * This service provides basic blacklisting functionality using in-memory storage.
 * It's suitable for development and single-instance deployments but does not
 * provide distributed blacklisting across multiple application instances.
 * 
 * ADR: This fallback ensures the application can function without Redis
 * while still providing basic security features. In production, Redis
 * should be used for distributed blacklisting.
 */
public class InMemoryJwtBlacklistService implements BlacklistService {
    
    private static final Logger logger = LoggerFactory.getLogger(InMemoryJwtBlacklistService.class);
    
    private final ConcurrentHashMap<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Instant> blacklistedUsers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
    
    public InMemoryJwtBlacklistService() {
        // Schedule cleanup every hour
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredEntries, 1, 1, TimeUnit.HOURS);
        logger.warn("Using in-memory JWT blacklist. For production, configure Redis for distributed blacklisting.");
    }
    
    /**
     * Blacklists a JWT token by storing its JTI in memory.
     * 
     * @param jti JWT ID to blacklist
     * @param expirationTime When the token expires naturally
     */
    public void blacklistToken(String jti, Instant expirationTime) {
        if (jti != null && expirationTime.isAfter(Instant.now())) {
            blacklistedTokens.put(jti, expirationTime);
            logger.debug("Token with JTI {} blacklisted in memory until {}", jti, expirationTime);
        }
    }
    
    /**
     * Checks if a JWT token is blacklisted.
     * 
     * @param jti JWT ID to check
     * @return true if the token is blacklisted, false otherwise
     */
    public boolean isTokenBlacklisted(String jti) {
        if (jti == null) {
            return false;
        }
        
        Instant expiration = blacklistedTokens.get(jti);
        if (expiration != null) {
            if (expiration.isAfter(Instant.now())) {
                logger.debug("Token with JTI {} is blacklisted", jti);
                return true;
            } else {
                // Token expired, remove from blacklist
                blacklistedTokens.remove(jti);
            }
        }
        
        return false;
    }
    
    /**
     * Blacklists all tokens for a specific user.
     * 
     * @param userId User ID whose tokens should be blacklisted
     * @param blacklistDuration How long to maintain the user blacklist
     */
    public void blacklistAllUserTokens(String userId, Duration blacklistDuration) {
        Instant expiration = Instant.now().plus(blacklistDuration);
        blacklistedUsers.put(userId, expiration);
        logger.info("All tokens blacklisted for user: {} until {}", userId, expiration);
    }
    
    /**
     * Checks if all tokens for a user are blacklisted.
     * 
     * @param userId User ID to check
     * @param tokenIssuedAt When the token was issued
     * @return true if user tokens are blacklisted and this token predates the blacklist
     */
    public boolean areUserTokensBlacklisted(String userId, Instant tokenIssuedAt) {
        if (userId == null) {
            return false;
        }
        
        Instant blacklistTime = blacklistedUsers.get(userId);
        if (blacklistTime != null) {
            if (blacklistTime.isAfter(Instant.now())) {
                // User is blacklisted, check if token was issued before blacklist
                return tokenIssuedAt.isBefore(blacklistTime.minus(Duration.ofHours(24))); // Approximate
            } else {
                // Blacklist expired, remove entry
                blacklistedUsers.remove(userId);
            }
        }
        
        return false;
    }
    
    /**
     * Gets statistics about the blacklist.
     * 
     * @return BlacklistStats containing current blacklist information
     */
    public JwtBlacklistService.BlacklistStats getBlacklistStats() {
        cleanupExpiredEntries(); // Clean up before counting
        int totalBlacklisted = blacklistedTokens.size() + blacklistedUsers.size();
        return new JwtBlacklistService.BlacklistStats(totalBlacklisted, 3600);
    }
    
    /**
     * Cleanup expired entries from memory.
     */
    public void cleanupExpiredEntries() {
        Instant now = Instant.now();
        
        // Clean up expired tokens
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
        
        // Clean up expired user blacklists
        blacklistedUsers.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
        
        logger.debug("Cleanup completed. Tokens: {}, Users: {}", 
                    blacklistedTokens.size(), blacklistedUsers.size());
    }
    
    /**
     * Shutdown the cleanup executor.
     */
    public void shutdown() {
        cleanupExecutor.shutdown();
    }
}