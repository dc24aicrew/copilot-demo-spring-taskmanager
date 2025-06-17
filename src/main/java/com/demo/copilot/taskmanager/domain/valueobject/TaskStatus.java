package com.demo.copilot.taskmanager.domain.valueobject;

/**
 * Enumeration representing task status values.
 */
public enum TaskStatus {
    TODO("To Do", "Task is planned but not started"),
    IN_PROGRESS("In Progress", "Task is currently being worked on"),
    IN_REVIEW("In Review", "Task is completed and under review"),
    COMPLETED("Completed", "Task is finished and approved"),
    CANCELLED("Cancelled", "Task has been cancelled");

    private final String displayName;
    private final String description;

    TaskStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isActive() {
        return this == TODO || this == IN_PROGRESS || this == IN_REVIEW;
    }

    public boolean canTransitionTo(TaskStatus newStatus) {
        return switch (this) {
            case TODO -> newStatus == IN_PROGRESS || newStatus == CANCELLED;
            case IN_PROGRESS -> newStatus == IN_REVIEW || newStatus == TODO || newStatus == CANCELLED;
            case IN_REVIEW -> newStatus == COMPLETED || newStatus == IN_PROGRESS;
            case COMPLETED -> newStatus == IN_PROGRESS; // Allow reopening
            case CANCELLED -> newStatus == TODO; // Allow reactivating
        };
    }
}