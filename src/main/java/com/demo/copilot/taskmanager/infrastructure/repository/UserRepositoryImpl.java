package com.demo.copilot.taskmanager.infrastructure.repository;

import com.demo.copilot.taskmanager.domain.entity.User;
import com.demo.copilot.taskmanager.domain.repository.UserRepositoryContract;
import com.demo.copilot.taskmanager.domain.valueobject.Email;
import com.demo.copilot.taskmanager.domain.valueobject.UserId;
import com.demo.copilot.taskmanager.infrastructure.persistence.entity.UserJpaEntity;
import com.demo.copilot.taskmanager.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.demo.copilot.taskmanager.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of UserRepositoryContract using JPA.
 * 
 * This implementation bridges the domain layer with the JPA persistence layer.
 */
@Repository
public class UserRepositoryImpl implements UserRepositoryContract {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper mapper;

    public UserRepositoryImpl(UserJpaRepository userJpaRepository, UserPersistenceMapper mapper) {
        this.userJpaRepository = userJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserJpaEntity jpaEntity = mapper.toJpaEntity(user);
        UserJpaEntity saved = userJpaRepository.save(jpaEntity);
        return mapper.toDomainEntity(saved);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return userJpaRepository.findById(id)
                .map(mapper::toDomainEntity);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return userJpaRepository.findByEmail(email)
                .map(mapper::toDomainEntity);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .map(mapper::toDomainEntity);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public List<User> findByIsActiveTrue() {
        return userJpaRepository.findByIsActiveTrue().stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findByIsActiveFalse() {
        return userJpaRepository.findByIsActiveFalse().stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findByRole(String role) {
        return userJpaRepository.findByRole(role).stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findUsersNotLoggedInSince(OffsetDateTime date) {
        return userJpaRepository.findUsersNotLoggedInSince(date).stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findUsersCreatedBetween(OffsetDateTime startDate, OffsetDateTime endDate) {
        return userJpaRepository.findUsersCreatedBetween(startDate, endDate).stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public long countActiveUsers() {
        return userJpaRepository.countActiveUsers();
    }

    @Override
    public long countUsersByRole(String role) {
        return userJpaRepository.countUsersByRole(role);
    }

    @Override
    public void deleteById(UserId id) {
        userJpaRepository.deleteById(id);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UserId id) {
        return userJpaRepository.existsById(id);
    }
}