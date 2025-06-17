package com.demo.copilot.taskmanager.domain.valueobject;

/**
 * Enumeration representing task categories.
 */
public enum TaskCategory {
    PERSONAL("Personal", "Personal tasks and activities"),
    WORK("Work", "Work-related tasks and projects"),
    PROJECT("Project", "Project-specific tasks"),
    MEETING("Meeting", "Meeting and discussion tasks"),
    RESEARCH("Research", "Research and analysis tasks"),
    DEVELOPMENT("Development", "Software development tasks"),
    TESTING("Testing", "Testing and quality assurance tasks"),
    DOCUMENTATION("Documentation", "Documentation and knowledge sharing"),
    MAINTENANCE("Maintenance", "System maintenance and support tasks"),
    OTHER("Other", "Miscellaneous tasks");

    private final String displayName;
    private final String description;

    TaskCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isWorkRelated() {
        return this == WORK || this == PROJECT || this == DEVELOPMENT || 
               this == TESTING || this == DOCUMENTATION || this == MAINTENANCE;
    }

    public boolean requiresCollaboration() {
        return this == PROJECT || this == MEETING || this == DEVELOPMENT || this == TESTING;
    }
}