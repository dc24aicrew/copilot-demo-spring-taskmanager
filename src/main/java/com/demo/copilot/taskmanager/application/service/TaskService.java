package com.demo.copilot.taskmanager.application.service;

import com.demo.copilot.taskmanager.application.dto.task.CreateTaskRequest;
import com.demo.copilot.taskmanager.application.dto.task.TaskResponse;
import com.demo.copilot.taskmanager.application.dto.task.TaskSummaryResponse;
import com.demo.copilot.taskmanager.application.dto.task.UpdateTaskRequest;
import com.demo.copilot.taskmanager.application.exception.TaskNotFoundException;
import com.demo.copilot.taskmanager.application.mapper.TaskMapper;
import com.demo.copilot.taskmanager.domain.entity.Task;
import com.demo.copilot.taskmanager.domain.valueobject.TaskId;
import com.demo.copilot.taskmanager.domain.valueobject.TaskStatus;
import com.demo.copilot.taskmanager.domain.valueobject.UserId;
import com.demo.copilot.taskmanager.infrastructure.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Application service for task management operations.
 * 
 * This service orchestrates task-related business operations and
 * coordinates between the domain layer and infrastructure layer.
 */
@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    /**
     * Create a new task.
     */
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public TaskResponse createTask(CreateTaskRequest request) {
        UserId currentUserId = getCurrentUserId();
        
        // Create task entity
        Task.Builder taskBuilder = new Task.Builder()
                .id(TaskId.generate())
                .title(request.getTitle())
                .description(request.getDescription())
                .status(TaskStatus.TODO)
                .priority(request.getPriority())
                .category(request.getCategory())
                .dueDate(request.getDueDate())
                .createdBy(currentUserId)
                .estimatedHours(request.getEstimatedHours())
                .isArchived(false);

        // Set assigned user if provided, otherwise assign to creator
        if (request.getAssignedTo() != null) {
            taskBuilder.assignedTo(UserId.of(request.getAssignedTo()));
        } else {
            taskBuilder.assignedTo(currentUserId);
        }

        Task task = taskBuilder.build();

        // Save and return response
        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponse(savedTask);
    }

    /**
     * Get task by ID.
     * Users can only view tasks they created or are assigned to, unless they are admin.
     */
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(UUID taskId) {
        Task task = findTaskById(TaskId.of(taskId));
        
        // Check access permissions
        validateTaskAccess(task, "view");
        
        return taskMapper.toResponse(task);
    }

    /**
     * Get all tasks with pagination.
     * Users see only tasks they created or are assigned to, unless they are admin.
     */
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Transactional(readOnly = true)
    public Page<TaskSummaryResponse> getAllTasks(Pageable pageable) {
        Page<Task> tasks;
        
        if (isAdmin()) {
            // Admins can see all non-archived tasks
            tasks = taskRepository.findByIsArchivedFalse(pageable);
        } else {
            // Regular users see only tasks they created or are assigned to
            UserId currentUserId = getCurrentUserId();
            tasks = taskRepository.findTasksAccessibleByUser(currentUserId, pageable);
        }
        
        return tasks.map(taskMapper::toSummaryResponse);
    }

    /**
     * Get tasks by status with pagination.
     */
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Transactional(readOnly = true)
    public Page<TaskSummaryResponse> getTasksByStatus(TaskStatus status, Pageable pageable) {
        Page<Task> tasks;
        
        if (isAdmin()) {
            // Admins can see all tasks with the given status
            tasks = taskRepository.findByStatus(status, pageable);
        } else {
            // Regular users see only their accessible tasks with the given status
            UserId currentUserId = getCurrentUserId();
            tasks = taskRepository.findByAssignedToAndStatus(currentUserId, status, pageable);
        }
        
        return tasks.map(taskMapper::toSummaryResponse);
    }

    /**
     * Update task.
     * Users can only update tasks they created, unless they are admin.
     */
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public TaskResponse updateTask(UUID taskId, UpdateTaskRequest request) {
        Task task = findTaskById(TaskId.of(taskId));
        
        // Check update permissions
        validateTaskAccess(task, "update");
        
        // Update fields if provided
        if (request.getTitle() != null) {
            task.updateDetails(request.getTitle(), task.getDescription());
        }
        if (request.getDescription() != null) {
            task.updateDetails(task.getTitle(), request.getDescription());
        }
        if (request.getStatus() != null) {
            task.updateStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.updatePriority(request.getPriority());
        }
        if (request.getAssignedTo() != null) {
            task.assignTo(UserId.of(request.getAssignedTo()));
        }
        // Note: For demo purposes, we're updating via the entity's business methods
        // In a full Clean Architecture, we'd have separate use cases
        
        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponse(savedTask);
    }

    /**
     * Delete task.
     * Users can only delete tasks they created, unless they are admin.
     */
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public void deleteTask(UUID taskId) {
        Task task = findTaskById(TaskId.of(taskId));
        
        // Check delete permissions
        validateTaskAccess(task, "delete");
        
        taskRepository.delete(task);
    }

    /**
     * Get tasks assigned to current user.
     */
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Transactional(readOnly = true)
    public Page<TaskSummaryResponse> getMyAssignedTasks(Pageable pageable) {
        UserId currentUserId = getCurrentUserId();
        Page<Task> tasks = taskRepository.findByAssignedTo(currentUserId, pageable);
        return tasks.map(taskMapper::toSummaryResponse);
    }

    /**
     * Get tasks created by current user.
     */
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Transactional(readOnly = true)
    public Page<TaskSummaryResponse> getMyCreatedTasks(Pageable pageable) {
        UserId currentUserId = getCurrentUserId();
        Page<Task> tasks = taskRepository.findByCreatedBy(currentUserId, pageable);
        return tasks.map(taskMapper::toSummaryResponse);
    }

    // Private helper methods

    private Task findTaskById(TaskId taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    private void validateTaskAccess(Task task, String operation) {
        if (isAdmin()) {
            return; // Admins have full access
        }
        
        UserId currentUserId = getCurrentUserId();
        boolean hasAccess = false;
        
        switch (operation) {
            case "view":
                hasAccess = task.isCreatedBy(currentUserId) || task.isAssignedTo(currentUserId);
                break;
            case "update":
            case "delete":
                hasAccess = task.isCreatedBy(currentUserId);
                break;
        }
        
        if (!hasAccess) {
            throw new TaskNotFoundException("Task not found with id: " + task.getId().getValue());
        }
    }

    private UserId getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        // For demo purposes, we'll use username as UUID (this would normally be mapped through UserService)
        // In a real application, we'd have a proper user context service
        try {
            return UserId.of(UUID.fromString(username));
        } catch (IllegalArgumentException e) {
            // Fallback: generate a UUID from username for demo
            return UserId.of(UUID.nameUUIDFromBytes(username.getBytes()));
        }
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}