package com.demo.copilot.taskmanager.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 configuration for the Task Manager API.
 * 
 * This configuration sets up:
 * - API documentation metadata
 * - JWT Bearer authentication scheme
 * - Server information
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Task Manager API",
        description = "Clean Architecture Demo for GitHub Copilot - Complete Task Management System with JWT Authentication",
        version = "1.0.0",
        contact = @Contact(
            name = "Demo Team",
            email = "demo@example.com"
        )
    ),
    servers = {
        @Server(url = "/api", description = "Task Manager API Server")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "JWT authentication via Bearer token. Obtain token from /api/auth/login endpoint."
)
public class OpenApiConfig {
    // Configuration class - no additional beans needed
    // The annotations above configure the OpenAPI documentation
}