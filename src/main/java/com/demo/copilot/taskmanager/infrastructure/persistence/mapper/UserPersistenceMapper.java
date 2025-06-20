package com.demo.copilot.taskmanager.infrastructure.persistence.mapper;

import com.demo.copilot.taskmanager.domain.entity.User;
import com.demo.copilot.taskmanager.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper between User domain entity and UserJpaEntity.
 * 
 * This mapper handles the conversion between the pure domain model
 * and the JPA persistence model.
 */
@Component
public class UserPersistenceMapper {

    /**
     * Convert from domain User to JPA entity.
     */
    public UserJpaEntity toJpaEntity(User domainUser) {
        if (domainUser == null) {
            return null;
        }

        UserJpaEntity jpaEntity = new UserJpaEntity();
        jpaEntity.setId(domainUser.getId());
        jpaEntity.setUsername(domainUser.getUsername());
        jpaEntity.setEmail(domainUser.getEmail());
        jpaEntity.setPasswordHash(domainUser.getPasswordHash());
        jpaEntity.setFirstName(domainUser.getFirstName());
        jpaEntity.setLastName(domainUser.getLastName());
        jpaEntity.setRole(domainUser.getRole());
        jpaEntity.setIsActive(domainUser.getIsActive());
        jpaEntity.setLastLoginAt(domainUser.getLastLoginAt());
        jpaEntity.setAvatarUrl(domainUser.getAvatarUrl());
        jpaEntity.setCreatedAt(domainUser.getCreatedAt());
        jpaEntity.setUpdatedAt(domainUser.getUpdatedAt());
        jpaEntity.setVersion(domainUser.getVersion());

        return jpaEntity;
    }

    /**
     * Convert from JPA entity to domain User.
     */
    public User toDomainEntity(UserJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        return new User.Builder()
                .id(jpaEntity.getId())
                .username(jpaEntity.getUsername())
                .email(jpaEntity.getEmail())
                .passwordHash(jpaEntity.getPasswordHash())
                .firstName(jpaEntity.getFirstName())
                .lastName(jpaEntity.getLastName())
                .role(jpaEntity.getRole())
                .isActive(jpaEntity.getIsActive())
                .avatarUrl(jpaEntity.getAvatarUrl())
                .build();
    }
}