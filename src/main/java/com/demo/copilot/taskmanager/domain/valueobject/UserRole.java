package com.demo.copilot.taskmanager.domain.valueobject;

/**
 * Enumeration representing user roles in the system.
 * 
 * Defines the different permission levels and access rights.
 */
public enum UserRole {
    USER("User", "Basic user with limited permissions"),
    MANAGER("Manager", "Manager with team oversight capabilities"),
    ADMIN("Administrator", "System administrator with full access");

    private final String displayName;
    private final String description;

    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasAdminPrivileges() {
        return this == ADMIN;
    }

    public boolean hasManagerPrivileges() {
        return this == ADMIN || this == MANAGER;
    }

    public boolean canManageUsers() {
        return this == ADMIN;
    }

    public boolean canManageTasks() {
        return this == ADMIN || this == MANAGER;
    }

    public boolean canViewAllTasks() {
        return this == ADMIN || this == MANAGER;
    }
}