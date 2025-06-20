package com.demo.copilot.taskmanager.application.dto.task;

import com.demo.copilot.taskmanager.domain.valueobject.TaskCategory;
import com.demo.copilot.taskmanager.domain.valueobject.TaskPriority;
import com.demo.copilot.taskmanager.domain.valueobject.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for task responses.
 */
@Schema(description = "Task response with full details")
public class TaskResponse {

    @Schema(description = "Task unique identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Task title", example = "Implement user authentication")
    private String title;

    @Schema(description = "Task description", example = "Add JWT-based authentication system")
    private String description;

    @Schema(description = "Task status", example = "IN_PROGRESS")
    private TaskStatus status;

    @Schema(description = "Task priority level", example = "HIGH")
    private TaskPriority priority;

    @Schema(description = "Task category", example = "DEVELOPMENT")
    private TaskCategory category;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @Schema(description = "Task due date", example = "2025-07-01T10:00:00+00:00")
    private OffsetDateTime dueDate;

    @Schema(description = "User ID assigned to the task", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID assignedTo;

    @Schema(description = "User ID who created the task", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID createdBy;

    @Schema(description = "Estimated hours to complete the task", example = "8")
    private Integer estimatedHours;

    @Schema(description = "Actual hours spent on the task", example = "6")
    private Integer actualHours;

    @Schema(description = "Whether the task is archived", example = "false")
    private Boolean isArchived;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @Schema(description = "Task completion date", example = "2025-06-30T15:30:00+00:00")
    private OffsetDateTime completedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @Schema(description = "Task creation date", example = "2025-06-20T09:00:00+00:00")
    private OffsetDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @Schema(description = "Task last update date", example = "2025-06-25T14:20:00+00:00")
    private OffsetDateTime updatedAt;

    @Schema(description = "Task version for optimistic locking", example = "1")
    private Long version;

    // Default constructor
    public TaskResponse() {}

    // Constructor
    public TaskResponse(UUID id, String title, String description, TaskStatus status,
                       TaskPriority priority, TaskCategory category, OffsetDateTime dueDate,
                       UUID assignedTo, UUID createdBy, Integer estimatedHours, Integer actualHours,
                       Boolean isArchived, OffsetDateTime completedAt, OffsetDateTime createdAt,
                       OffsetDateTime updatedAt, Long version) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.category = category;
        this.dueDate = dueDate;
        this.assignedTo = assignedTo;
        this.createdBy = createdBy;
        this.estimatedHours = estimatedHours;
        this.actualHours = actualHours;
        this.isArchived = isArchived;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public TaskCategory getCategory() {
        return category;
    }

    public void setCategory(TaskCategory category) {
        this.category = category;
    }

    public OffsetDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(OffsetDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public UUID getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UUID assignedTo) {
        this.assignedTo = assignedTo;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Integer estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public Integer getActualHours() {
        return actualHours;
    }

    public void setActualHours(Integer actualHours) {
        this.actualHours = actualHours;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(OffsetDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}