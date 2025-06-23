package com.demo.copilot.taskmanager.infrastructure.persistence.repository;

import com.demo.copilot.taskmanager.domain.valueobject.TaskId;
import com.demo.copilot.taskmanager.domain.valueobject.TaskStatus;
import com.demo.copilot.taskmanager.domain.valueobject.UserId;
import com.demo.copilot.taskmanager.infrastructure.persistence.entity.TaskJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA repository interface for TaskJpaEntity data access operations.
 */
@Repository
public interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, TaskId> {

    /**
     * Find tasks assigned to a specific user with pagination.
     */
    Page<TaskJpaEntity> findByAssignedTo(UserId assignedTo, Pageable pageable);

    /**
     * Find tasks by status.
     */
    List<TaskJpaEntity> findByStatus(TaskStatus status);

    /**
     * Find tasks created by a specific user with pagination.
     */
    Page<TaskJpaEntity> findByCreatedBy(UserId createdBy, Pageable pageable);

    /**
     * Find tasks by status with pagination.
     */
    Page<TaskJpaEntity> findByStatus(TaskStatus status, Pageable pageable);

    /**
     * Find all non-archived tasks with pagination.
     */
    Page<TaskJpaEntity> findByIsArchivedFalse(Pageable pageable);

    /**
     * Find tasks assigned to user or created by user with pagination.
     */
    @Query("SELECT t FROM TaskJpaEntity t WHERE (t.assignedTo = :assignedTo OR t.createdBy = :createdBy) AND t.isArchived = false")
    Page<TaskJpaEntity> findByAssignedToOrCreatedBy(@Param("assignedTo") UserId assignedTo, 
                                                     @Param("createdBy") UserId createdBy, 
                                                     Pageable pageable);

    /**
     * Count tasks by status.
     */
    @Query("SELECT COUNT(t) FROM TaskJpaEntity t WHERE t.status = :status")
    long countByStatus(@Param("status") TaskStatus status);

    /**
     * Count tasks assigned to a user.
     */
    @Query("SELECT COUNT(t) FROM TaskJpaEntity t WHERE t.assignedTo = :userId AND t.isArchived = false")
    long countByAssignedTo(@Param("userId") UserId userId);
}