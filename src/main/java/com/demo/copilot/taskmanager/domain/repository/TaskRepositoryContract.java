package com.demo.copilot.taskmanager.domain.repository;

import com.demo.copilot.taskmanager.domain.entity.Task;
import com.demo.copilot.taskmanager.domain.valueobject.TaskId;
import com.demo.copilot.taskmanager.domain.valueobject.TaskStatus;
import com.demo.copilot.taskmanager.domain.valueobject.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository contract for Task entity.
 * 
 * This interface defines the persistence operations required by the domain layer
 * without depending on any infrastructure framework.
 */
public interface TaskRepositoryContract {

    /**
     * Save a task entity.
     */
    Task save(Task task);

    /**
     * Find a task by ID.
     */
    Optional<Task> findById(TaskId id);

    /**
     * Find tasks assigned to a specific user with pagination.
     */
    Page<Task> findByAssignedTo(UserId assignedTo, Pageable pageable);

    /**
     * Find tasks by status.
     */
    List<Task> findByStatus(TaskStatus status);

    /**
     * Find tasks created by a specific user with pagination.
     */
    Page<Task> findByCreatedBy(UserId createdBy, Pageable pageable);

    /**
     * Find tasks by status with pagination.
     */
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    /**
     * Find all non-archived tasks with pagination.
     */
    Page<Task> findByIsArchivedFalse(Pageable pageable);

    /**
     * Find tasks assigned to user or created by user with pagination.
     */
    Page<Task> findByAssignedToOrCreatedBy(UserId assignedTo, UserId createdBy, Pageable pageable);

    /**
     * Count tasks by status.
     */
    long countByStatus(TaskStatus status);

    /**
     * Count tasks assigned to a user.
     */
    long countByAssignedTo(UserId userId);

    /**
     * Delete a task by ID.
     */
    void deleteById(TaskId id);

    /**
     * Find all tasks.
     */
    List<Task> findAll();

    /**
     * Find all tasks with pagination.
     */
    Page<Task> findAll(Pageable pageable);

    /**
     * Find tasks accessible by user (assigned to or created by) with pagination.
     */
    Page<Task> findTasksAccessibleByUser(UserId userId, Pageable pageable);

    /**
     * Find tasks by assigned user and status with pagination.
     */
    Page<Task> findByAssignedToAndStatus(UserId assignedTo, TaskStatus status, Pageable pageable);

    /**
     * Delete a task entity.
     */
    void delete(Task task);
}