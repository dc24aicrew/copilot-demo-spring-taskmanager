package com.demo.copilot.taskmanager.domain.valueobject;

/**
 * Enumeration representing task priority levels.
 */
public enum TaskPriority {
    LOW(1, "Low", "Low priority task"),
    MEDIUM(2, "Medium", "Medium priority task"),
    HIGH(3, "High", "High priority task"),
    URGENT(4, "Urgent", "Urgent priority task requiring immediate attention");

    private final int level;
    private final String displayName;
    private final String description;

    TaskPriority(int level, String displayName, String description) {
        this.level = level;
        this.displayName = displayName;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isHigherThan(TaskPriority other) {
        return this.level > other.level;
    }

    public boolean isLowerThan(TaskPriority other) {
        return this.level < other.level;
    }

    public boolean isCritical() {
        return this == HIGH || this == URGENT;
    }
}