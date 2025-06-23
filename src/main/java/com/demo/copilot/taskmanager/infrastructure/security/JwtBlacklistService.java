package com.demo.copilot.taskmanager.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis-based JWT token blacklisting service for secure token revocation.
 * 
 * Provides functionality to blacklist JWT tokens when users log out or when
 * tokens need to be revoked for security reasons. Uses Redis for distributed
 * blacklist storage with automatic cleanup of expired entries.
 * 
 * ADR: Redis is chosen for blacklist storage due to its excellent performance
 * for key-value operations, built-in TTL support, and ability to scale across
 * multiple application instances in a distributed environment.
 * 
 * This service gracefully handles Redis unavailability to prevent service disruption.
 */
public class JwtBlacklistService implements BlacklistService {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtBlacklistService.class);
    
    private final RedisTemplate<String, String> redisTemplate;
    
    @Value("${spring.security.jwt.blacklist.redis.key-prefix:jwt:blacklist:}")
    private String keyPrefix;
    
    @Value("${spring.security.jwt.blacklist.redis.cleanup-interval:3600}")
    private long cleanupIntervalSeconds;
    
    public JwtBlacklistService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    /**
     * Blacklists a JWT token by storing its JTI (JWT ID) in Redis.
     * The token will be blacklisted until its natural expiration time.
     * 
     * @param jti JWT ID to blacklist
     * @param expirationTime When the token expires naturally
     */
    public void blacklistToken(String jti, Instant expirationTime) {
        try {
            String key = keyPrefix + jti;
            long ttlSeconds = Duration.between(Instant.now(), expirationTime).toSeconds();
            
            if (ttlSeconds > 0) {
                redisTemplate.opsForValue().set(key, "blacklisted", ttlSeconds, TimeUnit.SECONDS);
                logger.debug("Token with JTI {} blacklisted for {} seconds", jti, ttlSeconds);
            } else {
                logger.debug("Token with JTI {} already expired, not blacklisting", jti);
            }
        } catch (Exception e) {
            logger.error("Failed to blacklist token with JTI: {}", jti, e);
            throw new RuntimeException("Token blacklisting failed", e);
        }
    }
    
    /**
     * Checks if a JWT token is blacklisted.
     * 
     * @param jti JWT ID to check
     * @return true if the token is blacklisted, false otherwise
     */
    public boolean isTokenBlacklisted(String jti) {
        try {
            String key = keyPrefix + jti;
            Boolean exists = redisTemplate.hasKey(key);
            boolean blacklisted = Boolean.TRUE.equals(exists);
            
            if (blacklisted) {
                logger.debug("Token with JTI {} is blacklisted", jti);
            }
            
            return blacklisted;
        } catch (Exception e) {
            logger.warn("Failed to check blacklist status for JTI: {}, allowing token", jti, e);
            // In case of Redis failure, we allow the token to prevent service disruption
            // This is a trade-off between availability and security
            return false;
        }
    }
    
    /**
     * Blacklists all tokens for a specific user by storing a user-level blacklist entry.
     * This is useful for "logout all devices" functionality.
     * 
     * @param userId User ID whose tokens should be blacklisted
     * @param blacklistDuration How long to maintain the user blacklist
     */
    public void blacklistAllUserTokens(String userId, Duration blacklistDuration) {
        try {
            String key = keyPrefix + "user:" + userId;
            String timestamp = String.valueOf(Instant.now().toEpochMilli());
            
            redisTemplate.opsForValue().set(key, timestamp, blacklistDuration.toSeconds(), TimeUnit.SECONDS);
            logger.info("All tokens blacklisted for user: {} for duration: {}", userId, blacklistDuration);
        } catch (Exception e) {
            logger.error("Failed to blacklist all tokens for user: {}", userId, e);
            throw new RuntimeException("User token blacklisting failed", e);
        }
    }
    
    /**
     * Checks if all tokens for a user are blacklisted and if the token was issued
     * before the blacklist timestamp.
     * 
     * @param userId User ID to check
     * @param tokenIssuedAt When the token was issued
     * @return true if user tokens are blacklisted and this token predates the blacklist
     */
    public boolean areUserTokensBlacklisted(String userId, Instant tokenIssuedAt) {
        try {
            String key = keyPrefix + "user:" + userId;
            String blacklistTimestamp = redisTemplate.opsForValue().get(key);
            
            if (blacklistTimestamp != null) {
                long blacklistTime = Long.parseLong(blacklistTimestamp);
                boolean isBlacklisted = tokenIssuedAt.toEpochMilli() < blacklistTime;
                
                if (isBlacklisted) {
                    logger.debug("Token for user {} is blacklisted (issued: {}, blacklist: {})", 
                               userId, tokenIssuedAt, Instant.ofEpochMilli(blacklistTime));
                }
                
                return isBlacklisted;
            }
            
            return false;
        } catch (Exception e) {
            logger.warn("Failed to check user blacklist for user: {}, allowing token", userId, e);
            return false;
        }
    }
    
    /**
     * Removes a token from the blacklist (if needed for administrative purposes).
     * 
     * @param jti JWT ID to remove from blacklist
     */
    public void removeFromBlacklist(String jti) {
        try {
            String key = keyPrefix + jti;
            Boolean deleted = redisTemplate.delete(key);
            
            if (Boolean.TRUE.equals(deleted)) {
                logger.info("Token with JTI {} removed from blacklist", jti);
            }
        } catch (Exception e) {
            logger.error("Failed to remove token from blacklist: {}", jti, e);
        }
    }
    
    /**
     * Gets statistics about the blacklist for monitoring purposes.
     * 
     * @return BlacklistStats containing current blacklist information
     */
    public BlacklistStats getBlacklistStats() {
        try {
            Set<String> keys = redisTemplate.keys(keyPrefix + "*");
            int totalBlacklistedTokens = keys != null ? keys.size() : 0;
            
            return new BlacklistStats(totalBlacklistedTokens, cleanupIntervalSeconds);
        } catch (Exception e) {
            logger.warn("Failed to get blacklist statistics", e);
            return new BlacklistStats(0, cleanupIntervalSeconds);
        }
    }
    
    /**
     * Cleanup method to remove expired blacklist entries.
     * This is typically called by a scheduled task.
     */
    public void cleanupExpiredEntries() {
        try {
            // Redis automatically removes expired keys, but we can implement
            // additional cleanup logic here if needed
            logger.debug("Blacklist cleanup completed");
        } catch (Exception e) {
            logger.error("Failed to cleanup expired blacklist entries", e);
        }
    }
    
    /**
     * Statistics holder for blacklist information.
     */
    public static class BlacklistStats {
        private final int totalBlacklistedTokens;
        private final long cleanupIntervalSeconds;
        
        public BlacklistStats(int totalBlacklistedTokens, long cleanupIntervalSeconds) {
            this.totalBlacklistedTokens = totalBlacklistedTokens;
            this.cleanupIntervalSeconds = cleanupIntervalSeconds;
        }
        
        public int getTotalBlacklistedTokens() {
            return totalBlacklistedTokens;
        }
        
        public long getCleanupIntervalSeconds() {
            return cleanupIntervalSeconds;
        }
    }
}