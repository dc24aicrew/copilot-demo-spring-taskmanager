package com.demo.copilot.taskmanager.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a Task identifier.
 */
@Embeddable
public class TaskId {

    @Column(name = "id")
    private UUID value;

    protected TaskId() {}

    private TaskId(UUID value) {
        this.value = Objects.requireNonNull(value, "Task ID value cannot be null");
    }

    public static TaskId generate() {
        return new TaskId(UUID.randomUUID());
    }

    public static TaskId of(UUID value) {
        return new TaskId(value);
    }

    public static TaskId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID string cannot be null or empty");
        }
        try {
            return new TaskId(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Task ID format: " + value, e);
        }
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskId taskId = (TaskId) o;
        return Objects.equals(value, taskId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "null";
    }
}