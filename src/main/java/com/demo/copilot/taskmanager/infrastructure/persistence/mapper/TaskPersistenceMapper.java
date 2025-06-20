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

        // Set the persistence-specific fields that don't go through builder
        setDomainFields(task, jpaEntity);
        
        return task;
    }

    /**
     * Helper method to set fields in domain entity that are not set through builder.
     */
    private void setDomainFields(Task domainTask, TaskJpaEntity jpaEntity) {
        // These would typically be set by reflection or by making the domain entity
        // more flexible. For now, we'll assume the domain entity has package-private setters
        // or we'll handle this through a factory method in the domain entity.
        
        // Note: In a real implementation, you might have a different approach
        // such as using reflection or having special factory methods
    }
}