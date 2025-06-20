package com.demo.copilot.taskmanager.infrastructure.persistence.entity;

import com.demo.copilot.taskmanager.domain.valueobject.*;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * JPA entity for Task persistence.
 * 
 * This entity is part of the infrastructure layer and contains
 * all JPA-specific annotations and configuration.
 */
@Entity
@Table(name = "tasks", indexes = {
    @Index(name = "idx_task_assigned_to", columnList = "assigned_to_id"),
    @Index(name = "idx_task_created_by", columnList = "created_by_id"),
    @Index(name = "idx_task_status", columnList = "status"),
    @Index(name = "idx_task_priority", columnList = "priority"),
    @Index(name = "idx_task_due_date", columnList = "due_date")
})
@EntityListeners(AuditingEntityListener.class)
public class TaskJpaEntity {

    @EmbeddedId
    private TaskId id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private TaskPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private TaskCategory category;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "assigned_to_id"))
    private UserId assignedTo;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "created_by_id"))
    private UserId createdBy;

    @Column(name = "due_date")
    private OffsetDateTime dueDate;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @Column(name = "actual_hours")
    private Integer actualHours;

    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    // Default constructor for JPA
    public TaskJpaEntity() {}

    // Getters and setters
    public TaskId getId() { return id; }
    public void setId(TaskId id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }

    public TaskCategory getCategory() { return category; }
    public void setCategory(TaskCategory category) { this.category = category; }

    public UserId getAssignedTo() { return assignedTo; }
    public void setAssignedTo(UserId assignedTo) { this.assignedTo = assignedTo; }

    public UserId getCreatedBy() { return createdBy; }
    public void setCreatedBy(UserId createdBy) { this.createdBy = createdBy; }

    public OffsetDateTime getDueDate() { return dueDate; }
    public void setDueDate(OffsetDateTime dueDate) { this.dueDate = dueDate; }

    public OffsetDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(OffsetDateTime completedAt) { this.completedAt = completedAt; }

    public Integer getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(Integer estimatedHours) { this.estimatedHours = estimatedHours; }

    public Integer getActualHours() { return actualHours; }
    public void setActualHours(Integer actualHours) { this.actualHours = actualHours; }

    public Boolean getIsArchived() { return isArchived; }
    public void setIsArchived(Boolean isArchived) { this.isArchived = isArchived; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskJpaEntity that = (TaskJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}