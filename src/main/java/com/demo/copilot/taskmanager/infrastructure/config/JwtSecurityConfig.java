package com.demo.copilot.taskmanager.infrastructure.config;

import com.demo.copilot.taskmanager.infrastructure.security.KeyManagementService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for JWT security services initialization.
 */
@Configuration
public class JwtSecurityConfig {

    /**
     * Initialize RSA keys on application startup.
     */
    @Bean
    CommandLineRunner initializeKeys(KeyManagementService keyManagementService) {
        return args -> keyManagementService.initializeKeys();
    }
}