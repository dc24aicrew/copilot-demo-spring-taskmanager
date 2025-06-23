package com.demo.copilot.taskmanager.presentation.controller;

import com.demo.copilot.taskmanager.application.dto.user.CreateUserRequest;
import com.demo.copilot.taskmanager.application.dto.user.UserResponse;
import com.demo.copilot.taskmanager.application.service.UserService;
import com.demo.copilot.taskmanager.infrastructure.security.JwtService;
import com.demo.copilot.taskmanager.infrastructure.security.JwtSecurityService;
import com.demo.copilot.taskmanager.presentation.dto.request.LoginRequest;
import com.demo.copilot.taskmanager.presentation.dto.request.RefreshTokenRequest;
import com.demo.copilot.taskmanager.presentation.dto.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 * Enhanced with secure JWT token management and refresh token support.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User authentication and registration with enhanced JWT security")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService; // Legacy service for backward compatibility
    private final JwtSecurityService jwtSecurityService; // Enhanced security service
    
    @Value("${spring.security.jwt.security.access-token.expiration:900s}")
    private String accessTokenExpiration;

    public AuthController(UserService userService, 
                         AuthenticationManager authenticationManager,
                         JwtService jwtService,
                         JwtSecurityService jwtSecurityService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtSecurityService = jwtSecurityService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", 
               description = "Creates a new user account and returns JWT tokens for authentication")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody CreateUserRequest request) {
        try {
            UserResponse user = userService.createUser(request);
            
            // Generate JWT tokens for the new user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Use enhanced JWT service for new registrations
            String accessToken = jwtSecurityService.generateAccessToken(userDetails);
            String refreshToken = jwtSecurityService.generateRefreshToken(userDetails);
            
            AuthResponse response = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(parseDurationToSeconds(accessTokenExpiration))
                .user(user)
                .message("User registered successfully")
                .build();
            
            logger.info("User registered successfully: {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("User registration failed for email: {}", request.getEmail(), e);
            throw e;
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT tokens", 
               description = "Authenticates user credentials and returns access and refresh tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserResponse user = userService.getUserByEmail(request.getEmail());
            
            // Use enhanced JWT service for new logins
            String accessToken = jwtSecurityService.generateAccessToken(userDetails);
            String refreshToken = jwtSecurityService.generateRefreshToken(userDetails);
            
            AuthResponse response = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(parseDurationToSeconds(accessTokenExpiration))
                .user(user)
                .message("Login successful")
                .build();
            
            logger.info("User logged in successfully: {}", user.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.warn("Login failed for email: {}", request.getEmail());
            throw e;
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", 
               description = "Exchange a valid refresh token for a new access token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            
            // Validate refresh token
            if (!jwtSecurityService.validateToken(refreshToken)) {
                logger.warn("Invalid refresh token provided");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            String username = jwtSecurityService.extractUsername(refreshToken);
            if (username == null) {
                logger.warn("Could not extract username from refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            UserResponse user = userService.getUserByEmail(username);
            
            // Generate new access token (refresh token rotation could be added here)
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password("") // Not needed for token generation
                .authorities(user.getRole().name())
                .build();
            
            String newAccessToken = jwtSecurityService.generateAccessToken(userDetails);
            
            AuthResponse response = AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // Keep the same refresh token
                .expiresIn(parseDurationToSeconds(accessTokenExpiration))
                .user(user)
                .message("Token refreshed successfully")
                .build();
            
            logger.debug("Token refreshed successfully for user: {}", username);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.warn("Token refresh failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and blacklist current token", 
               description = "Invalidates the current access token by adding it to the blacklist",
               security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            if (token != null) {
                jwtSecurityService.blacklistToken(token);
                logger.info("User logged out successfully, token blacklisted");
            }
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Logout failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Logout from all devices", 
               description = "Invalidates all tokens for the current user across all devices",
               security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<Void> logoutAllDevices(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            if (token != null) {
                String username = jwtSecurityService.extractUsername(token);
                if (username != null) {
                    jwtSecurityService.blacklistAllUserTokens(username);
                    logger.info("All tokens blacklisted for user: {}", username);
                }
            }
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Logout all devices failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Extracts JWT token from Authorization header.
     * 
     * @param authHeader Authorization header value
     * @return JWT token without Bearer prefix, or null if invalid
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Parses duration string to seconds for expiresIn field.
     * 
     * @param duration Duration string (e.g., "900s", "15m")
     * @return Duration in seconds
     */
    private Long parseDurationToSeconds(String duration) {
        try {
            if (duration.endsWith("s")) {
                return Long.parseLong(duration.substring(0, duration.length() - 1));
            } else if (duration.endsWith("m")) {
                return Long.parseLong(duration.substring(0, duration.length() - 1)) * 60;
            } else if (duration.endsWith("h")) {
                return Long.parseLong(duration.substring(0, duration.length() - 1)) * 3600;
            } else {
                return Long.parseLong(duration); // Assume seconds
            }
        } catch (Exception e) {
            logger.warn("Failed to parse duration: {}, using default 900s", duration);
            return 900L; // Default 15 minutes
        }
    }
}