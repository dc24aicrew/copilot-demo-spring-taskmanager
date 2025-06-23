package com.demo.copilot.taskmanager.infrastructure.security;

import com.demo.copilot.taskmanager.domain.entity.User;
import com.demo.copilot.taskmanager.domain.valueobject.Email;
import com.demo.copilot.taskmanager.domain.repository.UserRepositoryContract;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetailsService implementation for Spring Security.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepositoryContract userRepository;

    public UserDetailsServiceImpl(UserRepositoryContract userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        
        // Try to find by email first, then by username
        if (username.contains("@")) {
            user = userRepository.findByEmail(Email.of(username))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        } else {
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        }

        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("User account is deactivated: " + username);
        }

        return new CustomUserPrincipal(user);
    }

    /**
     * Custom UserDetails implementation
     */
    public static class CustomUserPrincipal implements UserDetails {
        private final User user;

        public CustomUserPrincipal(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            );
        }

        @Override
        public String getPassword() {
            return user.getPasswordHash();
        }

        @Override
        public String getUsername() {
            return user.getEmail().getValue();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return user.getIsActive();
        }

        public User getUser() {
            return user;
        }
    }
}