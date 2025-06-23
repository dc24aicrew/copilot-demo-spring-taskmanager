package com.demo.copilot.taskmanager.infrastructure.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Enhanced JWT Security Service implementing RS256 algorithm with comprehensive
 * security features including token blacklisting, refresh tokens, and enhanced validation.
 * 
 * This service replaces the basic HS256 implementation with enterprise-grade
 * security following OWASP JWT security guidelines.
 * 
 * ADR: We implement RS256 (RSA with SHA-256) instead of HS256 (HMAC) to enable:
 * - Asymmetric cryptography with public/private key pairs
 * - Better key distribution and rotation capabilities  
 * - Enhanced security against token forgery
 * - Compliance with modern JWT security standards
 */
@Service
public class JwtSecurityService {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtSecurityService.class);
    
    private final KeyManagementService keyManagementService;
    private final BlacklistService blacklistService;
    
    @Value("${spring.security.jwt.security.issuer:task-manager-api}")
    private String issuer;
    
    @Value("${spring.security.jwt.security.audience:task-manager-clients}")
    private String audience;
    
    @Value("${spring.security.jwt.security.access-token.expiration:900s}")
    private String accessTokenExpiration;
    
    @Value("${spring.security.jwt.security.refresh-token.expiration:604800s}")
    private String refreshTokenExpiration;
    
    public JwtSecurityService(KeyManagementService keyManagementService, 
                             BlacklistService blacklistService) {
        this.keyManagementService = keyManagementService;
        this.blacklistService = blacklistService;
    }
    
    /**
     * Generates a secure access token using RS256 algorithm with comprehensive claims.
     * 
     * @param userDetails User details for token generation
     * @return Signed JWT access token
     */
    public String generateAccessToken(UserDetails userDetails) {
        try {
            Instant now = Instant.now();
            Instant expiration = now.plus(parseDuration(accessTokenExpiration), ChronoUnit.SECONDS);
            
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userDetails.getUsername())
                    .issuer(issuer)
                    .audience(audience)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(expiration))
                    .notBeforeTime(Date.from(now))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("type", "access")
                    .claim("authorities", userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()))
                    .build();
            
            return signToken(claimsSet);
        } catch (Exception e) {
            logger.error("Failed to generate access token for user: {}", userDetails.getUsername(), e);
            throw new RuntimeException("Access token generation failed", e);
        }
    }
    
    /**
     * Generates a refresh token for token rotation.
     * 
     * @param userDetails User details for token generation
     * @return Signed JWT refresh token
     */
    public String generateRefreshToken(UserDetails userDetails) {
        try {
            Instant now = Instant.now();
            Instant expiration = now.plus(parseDuration(refreshTokenExpiration), ChronoUnit.SECONDS);
            
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userDetails.getUsername())
                    .issuer(issuer)
                    .audience(audience)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(expiration))
                    .notBeforeTime(Date.from(now))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("type", "refresh")
                    .build();
            
            return signToken(claimsSet);
        } catch (Exception e) {
            logger.error("Failed to generate refresh token for user: {}", userDetails.getUsername(), e);
            throw new RuntimeException("Refresh token generation failed", e);
        }
    }
    
    /**
     * Validates a JWT token with comprehensive security checks.
     * 
     * @param token JWT token to validate
     * @return true if token is valid and not blacklisted
     */
    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            
            // Check if token is blacklisted
            String jti = claims.getJWTID();
            if (jti != null && blacklistService.isTokenBlacklisted(jti)) {
                logger.debug("Token validation failed: token is blacklisted (JTI: {})", jti);
                return false;
            }
            
            // Check if all user tokens are blacklisted
            String subject = claims.getSubject();
            Instant issuedAt = claims.getIssueTime().toInstant();
            if (blacklistService.areUserTokensBlacklisted(subject, issuedAt)) {
                logger.debug("Token validation failed: all user tokens blacklisted for user: {}", subject);
                return false;
            }
            
            // Verify signature and claims
            return verifyTokenSignature(signedJWT) && validateClaims(claims);
            
        } catch (Exception e) {
            logger.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Extracts username from a valid JWT token.
     * 
     * @param token JWT token
     * @return Username (subject) from token
     */
    public String extractUsername(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            logger.debug("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Extracts authorities from a JWT token.
     * 
     * @param token JWT token
     * @return List of authority strings
     */
    @SuppressWarnings("unchecked")
    public List<String> extractAuthorities(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            return (List<String>) claims.getClaim("authorities");
        } catch (Exception e) {
            logger.debug("Failed to extract authorities from token: {}", e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Extracts token expiration time.
     * 
     * @param token JWT token
     * @return Expiration instant or null if extraction fails
     */
    public Instant extractExpiration(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            return expiration != null ? expiration.toInstant() : null;
        } catch (Exception e) {
            logger.debug("Failed to extract expiration from token: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Extracts JWT ID for blacklisting purposes.
     * 
     * @param token JWT token
     * @return JWT ID or null if extraction fails
     */
    public String extractJwtId(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getJWTID();
        } catch (Exception e) {
            logger.debug("Failed to extract JWT ID from token: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Blacklists a token (for logout functionality).
     * 
     * @param token JWT token to blacklist
     */
    public void blacklistToken(String token) {
        try {
            String jti = extractJwtId(token);
            Instant expiration = extractExpiration(token);
            
            if (jti != null && expiration != null) {
                blacklistService.blacklistToken(jti, expiration);
                logger.debug("Token blacklisted successfully (JTI: {})", jti);
            } else {
                logger.warn("Failed to blacklist token: missing JTI or expiration");
            }
        } catch (Exception e) {
            logger.error("Failed to blacklist token", e);
            throw new RuntimeException("Token blacklisting failed", e);
        }
    }
    
    /**
     * Blacklists all tokens for a user (for logout all devices).
     * 
     * @param username Username whose tokens should be blacklisted
     */
    public void blacklistAllUserTokens(String username) {
        try {
            // Blacklist for longer than the longest possible token lifetime
            blacklistService.blacklistAllUserTokens(username, 
                java.time.Duration.ofSeconds(parseDuration(refreshTokenExpiration)));
            logger.info("All tokens blacklisted for user: {}", username);
        } catch (Exception e) {
            logger.error("Failed to blacklist all tokens for user: {}", username, e);
            throw new RuntimeException("User token blacklisting failed", e);
        }
    }
    
    /**
     * Signs a JWT claims set using the current RSA private key.
     * 
     * @param claimsSet JWT claims to sign
     * @return Signed JWT token string
     * @throws JOSEException if signing fails
     */
    private String signToken(JWTClaimsSet claimsSet) throws JOSEException {
        RSAKey rsaKey = keyManagementService.getCurrentKey();
        
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(rsaKey.getKeyID())
                .type(JOSEObjectType.JWT)
                .build();
        
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        signedJWT.sign(new RSASSASigner(rsaKey));
        
        return signedJWT.serialize();
    }
    
    /**
     * Verifies the signature of a signed JWT.
     * 
     * @param signedJWT JWT to verify
     * @return true if signature is valid
     */
    private boolean verifyTokenSignature(SignedJWT signedJWT) {
        try {
            JWSHeader header = signedJWT.getHeader();
            String keyId = header.getKeyID();
            
            RSAKey rsaKey;
            if (keyId != null) {
                rsaKey = keyManagementService.getKeyById(keyId);
                if (rsaKey == null) {
                    logger.debug("Token validation failed: unknown key ID: {}", keyId);
                    return false;
                }
            } else {
                rsaKey = keyManagementService.getCurrentKey();
            }
            
            return signedJWT.verify(new RSASSAVerifier(rsaKey));
        } catch (Exception e) {
            logger.debug("Token signature verification failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Validates JWT claims including issuer, audience, and timing.
     * 
     * @param claims JWT claims to validate
     * @return true if all claims are valid
     */
    private boolean validateClaims(JWTClaimsSet claims) {
        try {
            Instant now = Instant.now();
            
            // Check expiration
            Date expiration = claims.getExpirationTime();
            if (expiration == null || expiration.toInstant().isBefore(now)) {
                logger.debug("Token validation failed: token expired");
                return false;
            }
            
            // Check not before
            Date notBefore = claims.getNotBeforeTime();
            if (notBefore != null && notBefore.toInstant().isAfter(now)) {
                logger.debug("Token validation failed: token not yet valid");
                return false;
            }
            
            // Check issuer
            if (!issuer.equals(claims.getIssuer())) {
                logger.debug("Token validation failed: invalid issuer");
                return false;
            }
            
            // Check audience
            if (!claims.getAudience().contains(audience)) {
                logger.debug("Token validation failed: invalid audience");
                return false;
            }
            
            return true;
        } catch (Exception e) {
            logger.debug("Claims validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Parses duration string (e.g., "900s", "15m") to seconds.
     * 
     * @param duration Duration string
     * @return Duration in seconds
     */
    private long parseDuration(String duration) {
        if (duration.endsWith("s")) {
            return Long.parseLong(duration.substring(0, duration.length() - 1));
        } else if (duration.endsWith("m")) {
            return Long.parseLong(duration.substring(0, duration.length() - 1)) * 60;
        } else if (duration.endsWith("h")) {
            return Long.parseLong(duration.substring(0, duration.length() - 1)) * 3600;
        } else {
            return Long.parseLong(duration); // Assume seconds
        }
    }
}