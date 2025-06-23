package com.demo.copilot.taskmanager.infrastructure.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Enhanced JWT Authentication Filter for processing JWT tokens with security features.
 * 
 * This filter supports both legacy HS256 tokens and new RS256 tokens with blacklisting.
 * It extracts JWT tokens from the Authorization header, validates them against blacklist,
 * and sets up the security context for authenticated users.
 * 
 * ADR: We support both JWT services during migration to ensure backward compatibility
 * while enabling enhanced security features for new tokens.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtService jwtService; // Legacy service
    private final JwtSecurityService jwtSecurityService; // Enhanced service
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, 
                                  JwtSecurityService jwtSecurityService,
                                  UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.jwtSecurityService = jwtSecurityService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        final String jwt;
        final String username;

        // Check if Authorization header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token
        jwt = authHeader.substring(BEARER_PREFIX.length());

        try {
            // Try enhanced JWT service first (for RS256 tokens)
            if (jwtSecurityService.validateToken(jwt)) {
                username = jwtSecurityService.extractUsername(jwt);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Use authorities from token to avoid database lookup
                    List<String> authorities = jwtSecurityService.extractAuthorities(jwt);
                    List<GrantedAuthority> grantedAuthorities = authorities.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                    
                    // Create simplified user details from token
                    UserDetails tokenUserDetails = org.springframework.security.core.userdetails.User.builder()
                        .username(username)
                        .password("") // Not needed for token-based auth
                        .authorities(grantedAuthorities)
                        .build();
                    
                    setAuthentication(request, tokenUserDetails);
                    logger.debug("Authentication successful using enhanced JWT service for user: {}", username);
                }
            } else {
                // Fallback to legacy JWT service (for HS256 tokens)
                username = jwtService.getUsernameFromToken(jwt);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Load user details from database for legacy tokens
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                    // Validate token using legacy service
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        setAuthentication(request, userDetails);
                        logger.debug("Authentication successful using legacy JWT service for user: {}", username);
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("JWT authentication failed: {}", e.getMessage());
            // Don't set authentication - let the request proceed unauthenticated
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Sets authentication in the security context.
     * 
     * @param request HTTP request
     * @param userDetails User details to authenticate
     */
    private void setAuthentication(HttpServletRequest request, UserDetails userDetails) {
        // Create authentication token
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        
        // Set authentication details
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        
        // Set authentication in SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
