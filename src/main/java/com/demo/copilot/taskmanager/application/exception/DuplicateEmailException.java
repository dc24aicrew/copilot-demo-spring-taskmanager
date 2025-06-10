package com.demo.copilot.taskmanager.application.exception;

/**
 * Exception thrown when attempting to create a user with an email that already exists.
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super(message);
    }

    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}