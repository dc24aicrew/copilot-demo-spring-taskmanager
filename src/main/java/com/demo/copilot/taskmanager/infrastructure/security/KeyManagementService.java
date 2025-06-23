package com.demo.copilot.taskmanager.infrastructure.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Service for managing RSA key pairs used in JWT signing and verification.
 * 
 * Provides key generation, storage, rotation, and retrieval functionality
 * following security best practices for production environments.
 * 
 * ADR: We implement RSA key management to support RS256 algorithm migration
 * from the less secure HS256 HMAC approach. This enables proper public/private
 * key cryptography and supports key rotation strategies.
 */
@Service
public class KeyManagementService {
    
    private static final Logger logger = LoggerFactory.getLogger(KeyManagementService.class);
    
    private final AtomicReference<RSAKey> currentKey = new AtomicReference<>();
    private final ConcurrentHashMap<String, RSAKey> keyStore = new ConcurrentHashMap<>();
    
    @Value("${spring.security.jwt.security.issuer:task-manager-api}")
    private String issuer;
    
    /**
     * Initializes the key management service and generates initial RSA key pair.
     */
    public void initializeKeys() {
        try {
            RSAKey rsaKey = generateNewKeyPair();
            currentKey.set(rsaKey);
            keyStore.put(rsaKey.getKeyID(), rsaKey);
            logger.info("Initial RSA key pair generated with Key ID: {}", rsaKey.getKeyID());
        } catch (JOSEException e) {
            logger.error("Failed to initialize RSA keys", e);
            throw new RuntimeException("Key initialization failed", e);
        }
    }
    
    /**
     * Generates a new RSA key pair for JWT signing.
     * 
     * @return RSAKey containing both public and private key components
     * @throws JOSEException if key generation fails
     */
    public RSAKey generateNewKeyPair() throws JOSEException {
        return new RSAKeyGenerator(2048)
                .keyID(generateKeyId())
                .generate();
    }
    
    /**
     * Gets the current active RSA key for signing operations.
     * 
     * @return Current RSAKey or null if not initialized
     */
    public RSAKey getCurrentKey() {
        RSAKey key = currentKey.get();
        if (key == null) {
            initializeKeys();
            key = currentKey.get();
        }
        return key;
    }
    
    /**
     * Gets RSA key by Key ID for verification operations.
     * 
     * @param keyId The Key ID to look up
     * @return RSAKey if found, null otherwise
     */
    public RSAKey getKeyById(String keyId) {
        return keyStore.get(keyId);
    }
    
    /**
     * Gets the current RSA private key for signing.
     * 
     * @return RSAPrivateKey for signing operations
     */
    public RSAPrivateKey getCurrentPrivateKey() {
        try {
            RSAKey key = getCurrentKey();
            return key.toRSAPrivateKey();
        } catch (JOSEException e) {
            logger.error("Failed to extract private key", e);
            throw new RuntimeException("Private key extraction failed", e);
        }
    }
    
    /**
     * Gets the current RSA public key for verification.
     * 
     * @return RSAPublicKey for verification operations  
     */
    public RSAPublicKey getCurrentPublicKey() {
        try {
            RSAKey key = getCurrentKey();
            return key.toRSAPublicKey();
        } catch (JOSEException e) {
            logger.error("Failed to extract public key", e);
            throw new RuntimeException("Public key extraction failed", e);
        }
    }
    
    /**
     * Rotates to a new RSA key pair while maintaining the previous key for verification.
     * This enables graceful key rotation without invalidating existing tokens.
     * 
     * @return The new RSAKey that is now current
     */
    public RSAKey rotateKey() {
        try {
            RSAKey oldKey = currentKey.get();
            RSAKey newKey = generateNewKeyPair();
            
            // Store new key and update current reference
            keyStore.put(newKey.getKeyID(), newKey);
            currentKey.set(newKey);
            
            logger.info("Key rotation completed. Old Key ID: {}, New Key ID: {}", 
                       oldKey != null ? oldKey.getKeyID() : "none", newKey.getKeyID());
            
            return newKey;
        } catch (JOSEException e) {
            logger.error("Key rotation failed", e);
            throw new RuntimeException("Key rotation failed", e);
        }
    }
    
    /**
     * Removes old keys from the key store.
     * Should be called after ensuring no valid tokens use these keys.
     * 
     * @param keyId Key ID to remove
     */
    public void removeOldKey(String keyId) {
        if (keyStore.remove(keyId) != null) {
            logger.info("Removed old key with ID: {}", keyId);
        }
    }
    
    /**
     * Gets the current key ID for JWT header inclusion.
     * 
     * @return Current key ID string
     */
    public String getCurrentKeyId() {
        RSAKey key = getCurrentKey();
        return key != null ? key.getKeyID() : null;
    }
    
    /**
     * Generates a unique key identifier.
     * 
     * @return Unique key ID string
     */
    private String generateKeyId() {
        return issuer + "-" + System.currentTimeMillis();
    }
}