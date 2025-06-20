package com.demo.copilot.taskmanager.application.dto.task;

import com.demo.copilot.taskmanager.domain.valueobject.TaskCategory;
import com.demo.copilot.taskmanager.domain.valueobject.TaskPriority;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for task creation requests.
 */
@Schema(description = "Request to create a new task")
public class CreateTaskRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    @Schema(description = "Task title", example = "Implement user authentication", required = true)
    private String title;

    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    @Schema(description = "Task description", example = "Add JWT-based authentication system")
    private String description;

    @NotNull(message = "Priority is required")
    @Schema(description = "Task priority level", example = "HIGH", required = true)
    private TaskPriority priority;

    @Schema(description = "Task category", example = "DEVELOPMENT")
    private TaskCategory category;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @Schema(description = "Task due date", example = "2025-07-01T10:00:00+00:00")
    private OffsetDateTime dueDate;

    @Schema(description = "User ID to assign the task to", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID assignedTo;

    @Min(value = 1, message = "Estimated hours must be at least 1")
    @Max(value = 1000, message = "Estimated hours cannot exceed 1000")
    @Schema(description = "Estimated hours to complete the task", example = "8")
    private Integer estimatedHours;

    // Default constructor
    public CreateTaskRequest() {}

    // Constructor for testing
    public CreateTaskRequest(String title, String description, TaskPriority priority, 
                           TaskCategory category, OffsetDateTime dueDate, UUID assignedTo, 
                           Integer estimatedHours) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.category = category;
        this.dueDate = dueDate;
        this.assignedTo = assignedTo;
        this.estimatedHours = estimatedHours;
    }

    // Getters and Setters
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

    public Integer getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Integer estimatedHours) {
        this.estimatedHours = estimatedHours;
    }
}