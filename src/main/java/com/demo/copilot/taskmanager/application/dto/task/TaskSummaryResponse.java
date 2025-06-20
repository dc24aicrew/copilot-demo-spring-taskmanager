package com.demo.copilot.taskmanager.application.dto.task;

import com.demo.copilot.taskmanager.domain.valueobject.TaskCategory;
import com.demo.copilot.taskmanager.domain.valueobject.TaskPriority;
import com.demo.copilot.taskmanager.domain.valueobject.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for task summary responses used in list operations.
 */
@Schema(description = "Task summary response for list operations")
public class TaskSummaryResponse {

    @Schema(description = "Task unique identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Task title", example = "Implement user authentication")
    private String title;

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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @Schema(description = "Task creation date", example = "2025-06-20T09:00:00+00:00")
    private OffsetDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @Schema(description = "Task last update date", example = "2025-06-25T14:20:00+00:00")
    private OffsetDateTime updatedAt;

    // Default constructor
    public TaskSummaryResponse() {}

    // Constructor
    public TaskSummaryResponse(UUID id, String title, TaskStatus status, TaskPriority priority,
                              TaskCategory category, OffsetDateTime dueDate, UUID assignedTo,
                              UUID createdBy, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.category = category;
        this.dueDate = dueDate;
        this.assignedTo = assignedTo;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
}