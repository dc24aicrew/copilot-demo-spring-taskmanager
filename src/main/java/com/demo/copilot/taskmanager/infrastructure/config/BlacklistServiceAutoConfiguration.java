package com.demo.copilot.taskmanager.infrastructure.config;

import com.demo.copilot.taskmanager.infrastructure.security.BlacklistService;
import com.demo.copilot.taskmanager.infrastructure.security.InMemoryJwtBlacklistService;
import com.demo.copilot.taskmanager.infrastructure.security.JwtBlacklistService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Auto-configuration for blacklist services.
 * Provides Redis-based blacklisting when Redis is available,
 * falls back to in-memory blacklisting otherwise.
 */
@Configuration
public class BlacklistServiceAutoConfiguration {

    /**
     * Redis-based blacklist service when Redis is configured.
     */
    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    @ConditionalOnProperty(value = "spring.data.redis.host")
    public BlacklistService redisBlacklistService(RedisTemplate<String, String> redisTemplate) {
        return new JwtBlacklistService(redisTemplate);
    }
    
    /**
     * In-memory blacklist service when Redis is not available.
     */
    @Bean
    @ConditionalOnMissingBean(BlacklistService.class)
    public BlacklistService inMemoryBlacklistService() {
        return new InMemoryJwtBlacklistService();
    }
}