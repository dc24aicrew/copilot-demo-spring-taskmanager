package com.demo.copilot.taskmanager.application.exception;

/**
 * Exception thrown when attempting to create a user with a username that already exists.
 */
public class DuplicateUsernameException extends RuntimeException {

    public DuplicateUsernameException(String message) {
        super(message);
    }

    public DuplicateUsernameException(String message, Throwable cause) {
        super(message, cause);
    }
}