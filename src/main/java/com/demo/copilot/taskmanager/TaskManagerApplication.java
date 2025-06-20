package com.demo.copilot.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main application class for Task Manager Demo.
 * 
 * This application demonstrates Clean Architecture principles with Spring Boot
 * for GitHub Copilot showcases.
 */
@SpringBootApplication
@EnableCaching
public class TaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }
}