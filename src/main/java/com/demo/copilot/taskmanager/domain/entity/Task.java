package com.demo.copilot.taskmanager.domain.entity;

import com.demo.copilot.taskmanager.domain.valueobject.*;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Task domain entity representing a work item in the system.
 * 
 * Pure domain entity following Clean Architecture principles.
 * Contains business logic and rules related to task management
 * without any infrastructure dependencies.
 */
public class Task {

    private TaskId id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private TaskCategory category;
    private UserId assignedTo;
    private UserId createdBy;
    private OffsetDateTime dueDate;
    private OffsetDateTime completedAt;
    private Integer estimatedHours;
    private Integer actualHours;
    private Boolean isArchived;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Long version;

    // Default constructor
    protected Task() {}

    private Task(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.status = builder.status;
        this.priority = builder.priority;
        this.category = builder.category;
        this.assignedTo = builder.assignedTo;
        this.createdBy = builder.createdBy;
        this.dueDate = builder.dueDate;
        this.estimatedHours = builder.estimatedHours;
        this.isArchived = builder.isArchived;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        this.version = 0L;
    }

    // Business methods
    public void updateStatus(TaskStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Task status cannot be null");
        }
        
        if (this.status == TaskStatus.COMPLETED && newStatus != TaskStatus.COMPLETED) {
            this.completedAt = null;
        } else if (newStatus == TaskStatus.COMPLETED && this.status != TaskStatus.COMPLETED) {
            this.completedAt = OffsetDateTime.now();
        }
        
        this.status = newStatus;
        this.updatedAt = OffsetDateTime.now();
    }

    public void assignTo(UserId userId) {
        this.assignedTo = Objects.requireNonNull(userId, "Assigned user ID cannot be null");
        this.updatedAt = OffsetDateTime.now();
    }

    public void updatePriority(TaskPriority newPriority) {
        this.priority = Objects.requireNonNull(newPriority, "Priority cannot be null");
        this.updatedAt = OffsetDateTime.now();
    }

    public void updateDetails(String title, String description) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
        this.description = description != null ? description.trim() : null;
        this.updatedAt = OffsetDateTime.now();
    }

    public void setDueDate(OffsetDateTime dueDate) {
        this.dueDate = dueDate;
        this.updatedAt = OffsetDateTime.now();
    }

    public void updateTimeEstimate(Integer estimatedHours, Integer actualHours) {
        this.estimatedHours = estimatedHours != null && estimatedHours >= 0 ? estimatedHours : this.estimatedHours;
        this.actualHours = actualHours != null && actualHours >= 0 ? actualHours : this.actualHours;
        this.updatedAt = OffsetDateTime.now();
    }

    public void archive() {
        this.isArchived = true;
        this.updatedAt = OffsetDateTime.now();
    }

    public void unarchive() {
        this.isArchived = false;
        this.updatedAt = OffsetDateTime.now();
    }

    public void complete() {
        updateStatus(TaskStatus.COMPLETED);
    }

    // Business queries
    public boolean isOverdue() {
        return dueDate != null && 
               OffsetDateTime.now().isAfter(dueDate) && 
               status != TaskStatus.COMPLETED;
    }

    public boolean isDueSoon(int hoursThreshold) {
        if (dueDate == null || status == TaskStatus.COMPLETED) {
            return false;
        }
        return OffsetDateTime.now().plusHours(hoursThreshold).isAfter(dueDate);
    }

    public boolean isAssignedTo(UserId userId) {
        return Objects.equals(this.assignedTo, userId);
    }

    public boolean isCreatedBy(UserId userId) {
        return Objects.equals(this.createdBy, userId);
    }

    public boolean isHighPriority() {
        return priority == TaskPriority.HIGH || priority == TaskPriority.URGENT;
    }

    public boolean isCompleted() {
        return status == TaskStatus.COMPLETED;
    }

    public boolean isInProgress() {
        return status == TaskStatus.IN_PROGRESS;
    }

    // Getters
    public TaskId getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public TaskPriority getPriority() { return priority; }
    public TaskCategory getCategory() { return category; }
    public UserId getAssignedTo() { return assignedTo; }
    public UserId getCreatedBy() { return createdBy; }
    public OffsetDateTime getDueDate() { return dueDate; }
    public OffsetDateTime getCompletedAt() { return completedAt; }
    public Integer getEstimatedHours() { return estimatedHours; }
    public Integer getActualHours() { return actualHours; }
    public Boolean getIsArchived() { return isArchived; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", status=" + status +
               ", priority=" + priority +
               ", assignedTo=" + assignedTo +
               ", dueDate=" + dueDate +
               '}';
    }

    // Builder pattern
    public static class Builder {
        private TaskId id;
        private String title;
        private String description;
        private TaskStatus status = TaskStatus.TODO;
        private TaskPriority priority = TaskPriority.MEDIUM;
        private TaskCategory category;
        private UserId assignedTo;
        private UserId createdBy;
        private OffsetDateTime dueDate;
        private Integer estimatedHours;
        private Boolean isArchived = false;

        public Builder id(TaskId id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder status(TaskStatus status) {
            this.status = status;
            return this;
        }

        public Builder priority(TaskPriority priority) {
            this.priority = priority;
            return this;
        }

        public Builder category(TaskCategory category) {
            this.category = category;
            return this;
        }

        public Builder assignedTo(UserId assignedTo) {
            this.assignedTo = assignedTo;
            return this;
        }

        public Builder createdBy(UserId createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder dueDate(OffsetDateTime dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Builder estimatedHours(Integer estimatedHours) {
            this.estimatedHours = estimatedHours;
            return this;
        }

        public Builder isArchived(Boolean isArchived) {
            this.isArchived = isArchived;
            return this;
        }

        public Task build() {
            Objects.requireNonNull(id, "Task ID cannot be null");
            Objects.requireNonNull(title, "Title cannot be null");
            Objects.requireNonNull(assignedTo, "Assigned user cannot be null");
            Objects.requireNonNull(createdBy, "Created by user cannot be null");
            
            return new Task(this);
        }
    }
}