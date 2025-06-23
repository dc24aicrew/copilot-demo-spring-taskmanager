package com.demo.copilot.taskmanager.infrastructure.persistence.mapper;

import com.demo.copilot.taskmanager.domain.entity.Task;
import com.demo.copilot.taskmanager.infrastructure.persistence.entity.TaskJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper between Task domain entity and TaskJpaEntity.
 * 
 * This mapper handles the conversion between the pure domain model
 * and the JPA persistence model.
 */
@Component
public class TaskPersistenceMapper {

    /**
     * Convert from domain Task to JPA entity.
     */
    public TaskJpaEntity toJpaEntity(Task domainTask) {
        if (domainTask == null) {
            return null;
        }

        TaskJpaEntity jpaEntity = new TaskJpaEntity();
        jpaEntity.setId(domainTask.getId());
        jpaEntity.setTitle(domainTask.getTitle());
        jpaEntity.setDescription(domainTask.getDescription());
        jpaEntity.setStatus(domainTask.getStatus());
        jpaEntity.setPriority(domainTask.getPriority());
        jpaEntity.setCategory(domainTask.getCategory());
        jpaEntity.setAssignedTo(domainTask.getAssignedTo());
        jpaEntity.setCreatedBy(domainTask.getCreatedBy());
        jpaEntity.setDueDate(domainTask.getDueDate());
        jpaEntity.setCompletedAt(domainTask.getCompletedAt());
        jpaEntity.setEstimatedHours(domainTask.getEstimatedHours());
        jpaEntity.setActualHours(domainTask.getActualHours());
        jpaEntity.setIsArchived(domainTask.getIsArchived());
        jpaEntity.setCreatedAt(domainTask.getCreatedAt());
        jpaEntity.setUpdatedAt(domainTask.getUpdatedAt());
        jpaEntity.setVersion(domainTask.getVersion());

        return jpaEntity;
    }

    /**
     * Convert from JPA entity to domain Task.
     */
    public Task toDomainEntity(TaskJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        Task task = new Task.Builder()
                .id(jpaEntity.getId())
                .title(jpaEntity.getTitle())
                .description(jpaEntity.getDescription())
                .status(jpaEntity.getStatus())
                .priority(jpaEntity.getPriority())
                .category(jpaEntity.getCategory())
                .assignedTo(jpaEntity.getAssignedTo())
                .createdBy(jpaEntity.getCreatedBy())
                .dueDate(jpaEntity.getDueDate())
                .estimatedHours(jpaEntity.getEstimatedHours())
                .isArchived(jpaEntity.getIsArchived())
                .build();

        // Set persistence fields using reflection for now
        // In a real application, you'd have a more sophisticated approach
        setFieldValue(task, "completedAt", jpaEntity.getCompletedAt());
        setFieldValue(task, "actualHours", jpaEntity.getActualHours());
        setFieldValue(task, "createdAt", jpaEntity.getCreatedAt());
        setFieldValue(task, "updatedAt", jpaEntity.getUpdatedAt());
        setFieldValue(task, "version", jpaEntity.getVersion());
        
        return task;
    }

    /**
     * Helper method to set fields in domain entity using reflection.
     */
    private void setFieldValue(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            // Ignore - field might not exist or be accessible
        }
    }
}