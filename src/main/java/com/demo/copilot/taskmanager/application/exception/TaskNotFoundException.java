package com.demo.copilot.taskmanager.application.exception;

import com.demo.copilot.taskmanager.domain.valueobject.TaskId;

/**
 * Exception thrown when a requested task is not found.
 */
public class TaskNotFoundException extends RuntimeException {

    private final TaskId taskId;

    public TaskNotFoundException(TaskId taskId) {
        super("Task not found with id: " + taskId.getValue());
        this.taskId = taskId;
    }

    public TaskNotFoundException(String message) {
        super(message);
        this.taskId = null;
    }

    public TaskId getTaskId() {
        return taskId;
    }
}