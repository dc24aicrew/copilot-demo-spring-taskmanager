package com.demo.copilot.taskmanager.application.usecase.task;

import com.demo.copilot.taskmanager.domain.entity.Task;
import com.demo.copilot.taskmanager.domain.repository.TaskRepositoryContract;
import com.demo.copilot.taskmanager.domain.valueobject.TaskId;
import com.demo.copilot.taskmanager.domain.valueobject.TaskPriority;
import com.demo.copilot.taskmanager.domain.valueobject.TaskStatus;
import com.demo.copilot.taskmanager.domain.valueobject.UserId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Use case for creating a new task.
 * 
 * This class encapsulates the business logic for task creation
 * following Clean Architecture principles.
 */
@Component
@Transactional
public class CreateTaskUseCase {

    private final TaskRepositoryContract taskRepository;

    public CreateTaskUseCase(TaskRepositoryContract taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Execute the use case to create a new task.
     */
    public Task execute(CreateTaskCommand command) {
        Objects.requireNonNull(command, "Command cannot be null");
        
        // Business logic: Create task with proper defaults and validation
        Task task = new Task.Builder()
                .id(TaskId.generate())
                .title(validateAndCleanTitle(command.getTitle()))
                .description(command.getDescription())
                .status(TaskStatus.TODO)
                .priority(command.getPriority() != null ? command.getPriority() : TaskPriority.MEDIUM)
                .category(command.getCategory())
                .createdBy(command.getCreatedBy())
                .assignedTo(command.getAssignedTo() != null ? command.getAssignedTo() : command.getCreatedBy())
                .dueDate(command.getDueDate())
                .estimatedHours(command.getEstimatedHours())
                .isArchived(false)
                .build();

        return taskRepository.save(task);
    }

    private String validateAndCleanTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be null or empty");
        }
        
        String cleanTitle = title.trim();
        if (cleanTitle.length() > 200) {
            throw new IllegalArgumentException("Task title cannot exceed 200 characters");
        }
        
        return cleanTitle;
    }

    /**
     * Command object for creating a task.
     */
    public static class CreateTaskCommand {
        private final String title;
        private final String description;
        private final TaskPriority priority;
        private final com.demo.copilot.taskmanager.domain.valueobject.TaskCategory category;
        private final UserId createdBy;
        private final UserId assignedTo;
        private final OffsetDateTime dueDate;
        private final Integer estimatedHours;

        public CreateTaskCommand(String title, String description, TaskPriority priority,
                               com.demo.copilot.taskmanager.domain.valueobject.TaskCategory category,
                               UserId createdBy, UserId assignedTo, OffsetDateTime dueDate,
                               Integer estimatedHours) {
            this.title = title;
            this.description = description;
            this.priority = priority;
            this.category = category;
            this.createdBy = createdBy;
            this.assignedTo = assignedTo;
            this.dueDate = dueDate;
            this.estimatedHours = estimatedHours;
        }

        // Getters
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public TaskPriority getPriority() { return priority; }
        public com.demo.copilot.taskmanager.domain.valueobject.TaskCategory getCategory() { return category; }
        public UserId getCreatedBy() { return createdBy; }
        public UserId getAssignedTo() { return assignedTo; }
        public OffsetDateTime getDueDate() { return dueDate; }
        public Integer getEstimatedHours() { return estimatedHours; }
    }
}