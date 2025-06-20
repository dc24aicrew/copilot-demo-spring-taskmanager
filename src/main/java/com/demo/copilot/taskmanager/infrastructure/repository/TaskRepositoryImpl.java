package com.demo.copilot.taskmanager.infrastructure.repository;

import com.demo.copilot.taskmanager.domain.entity.Task;
import com.demo.copilot.taskmanager.domain.repository.TaskRepositoryContract;
import com.demo.copilot.taskmanager.domain.valueobject.TaskId;
import com.demo.copilot.taskmanager.domain.valueobject.TaskStatus;
import com.demo.copilot.taskmanager.domain.valueobject.UserId;
import com.demo.copilot.taskmanager.infrastructure.persistence.entity.TaskJpaEntity;
import com.demo.copilot.taskmanager.infrastructure.persistence.mapper.TaskPersistenceMapper;
import com.demo.copilot.taskmanager.infrastructure.persistence.repository.TaskJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of TaskRepositoryContract using JPA.
 * 
 * This implementation bridges the domain layer with the JPA persistence layer.
 */
@Repository
public class TaskRepositoryImpl implements TaskRepositoryContract {

    private final TaskJpaRepository taskJpaRepository;
    private final TaskPersistenceMapper mapper;

    public TaskRepositoryImpl(TaskJpaRepository taskJpaRepository, TaskPersistenceMapper mapper) {
        this.taskJpaRepository = taskJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Task save(Task task) {
        TaskJpaEntity jpaEntity = mapper.toJpaEntity(task);
        TaskJpaEntity saved = taskJpaRepository.save(jpaEntity);
        return mapper.toDomainEntity(saved);
    }

    @Override
    public Optional<Task> findById(TaskId id) {
        return taskJpaRepository.findById(id)
                .map(mapper::toDomainEntity);
    }

    @Override
    public Page<Task> findByAssignedTo(UserId assignedTo, Pageable pageable) {
        Page<TaskJpaEntity> jpaPage = taskJpaRepository.findByAssignedTo(assignedTo, pageable);
        List<Task> domainTasks = jpaPage.getContent().stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(domainTasks, pageable, jpaPage.getTotalElements());
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        return taskJpaRepository.findByStatus(status).stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Task> findByCreatedBy(UserId createdBy, Pageable pageable) {
        Page<TaskJpaEntity> jpaPage = taskJpaRepository.findByCreatedBy(createdBy, pageable);
        List<Task> domainTasks = jpaPage.getContent().stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(domainTasks, pageable, jpaPage.getTotalElements());
    }

    @Override
    public Page<Task> findByStatus(TaskStatus status, Pageable pageable) {
        Page<TaskJpaEntity> jpaPage = taskJpaRepository.findByStatus(status, pageable);
        List<Task> domainTasks = jpaPage.getContent().stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(domainTasks, pageable, jpaPage.getTotalElements());
    }

    @Override
    public Page<Task> findByIsArchivedFalse(Pageable pageable) {
        Page<TaskJpaEntity> jpaPage = taskJpaRepository.findByIsArchivedFalse(pageable);
        List<Task> domainTasks = jpaPage.getContent().stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(domainTasks, pageable, jpaPage.getTotalElements());
    }

    @Override
    public Page<Task> findByAssignedToOrCreatedBy(UserId assignedTo, UserId createdBy, Pageable pageable) {
        Page<TaskJpaEntity> jpaPage = taskJpaRepository.findByAssignedToOrCreatedBy(assignedTo, createdBy, pageable);
        List<Task> domainTasks = jpaPage.getContent().stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(domainTasks, pageable, jpaPage.getTotalElements());
    }

    @Override
    public long countByStatus(TaskStatus status) {
        return taskJpaRepository.countByStatus(status);
    }

    @Override
    public long countByAssignedTo(UserId userId) {
        return taskJpaRepository.countByAssignedTo(userId);
    }

    @Override
    public void deleteById(TaskId id) {
        taskJpaRepository.deleteById(id);
    }

    @Override
    public List<Task> findAll() {
        return taskJpaRepository.findAll().stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Task> findAll(Pageable pageable) {
        Page<TaskJpaEntity> jpaPage = taskJpaRepository.findAll(pageable);
        List<Task> domainTasks = jpaPage.getContent().stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(domainTasks, pageable, jpaPage.getTotalElements());
    }

    @Override
    public Page<Task> findTasksAccessibleByUser(UserId userId, Pageable pageable) {
        return findByAssignedToOrCreatedBy(userId, userId, pageable);
    }

    @Override
    public Page<Task> findByAssignedToAndStatus(UserId assignedTo, TaskStatus status, Pageable pageable) {
        // This is a new method that needs to be added to the JPA repository as well
        // For now, we'll implement it using a combination of existing methods
        Page<TaskJpaEntity> jpaPage = taskJpaRepository.findByAssignedTo(assignedTo, pageable);
        List<Task> filteredTasks = jpaPage.getContent().stream()
                .map(mapper::toDomainEntity)
                .filter(task -> task.getStatus().equals(status))
                .collect(Collectors.toList());
        return new PageImpl<>(filteredTasks, pageable, filteredTasks.size());
    }

    @Override
    public void delete(Task task) {
        TaskJpaEntity jpaEntity = mapper.toJpaEntity(task);
        taskJpaRepository.delete(jpaEntity);
    }
}