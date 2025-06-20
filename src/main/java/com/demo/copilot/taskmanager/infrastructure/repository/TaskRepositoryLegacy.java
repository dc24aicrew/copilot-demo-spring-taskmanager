package com.demo.copilot.taskmanager.infrastructure.repository;

import com.demo.copilot.taskmanager.domain.entity.Task;
import com.demo.copilot.taskmanager.domain.valueobject.TaskId;
import com.demo.copilot.taskmanager.domain.valueobject.TaskStatus;
import com.demo.copilot.taskmanager.domain.valueobject.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Legacy repository interface for Task entity data access operations.
 * 
 * @deprecated This interface violates Clean Architecture principles.
 * Use {@link com.demo.copilot.taskmanager.domain.repository.TaskRepositoryContract} instead.
 */
@Deprecated
@Repository
public interface TaskRepositoryLegacy extends JpaRepository<Task, TaskId> {

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
    @Query("SELECT t FROM Task t WHERE (t.assignedTo = :userId OR t.createdBy = :userId) AND t.isArchived = false")
    Page<Task> findTasksAccessibleByUser(@Param("userId") UserId userId, Pageable pageable);

    /**
     * Find tasks by assigned user and status.
     */
    Page<Task> findByAssignedToAndStatus(UserId assignedTo, TaskStatus status, Pageable pageable);

    /**
     * Find tasks by created user and status.
     */
    Page<Task> findByCreatedByAndStatus(UserId createdBy, TaskStatus status, Pageable pageable);

    /**
     * Count tasks by status.
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status AND t.isArchived = false")
    long countByStatus(@Param("status") TaskStatus status);

    /**
     * Count tasks assigned to a user.
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo = :userId AND t.isArchived = false")
    long countByAssignedTo(@Param("userId") UserId userId);
}