package com.demo.copilot.taskmanager.infrastructure.repository;

import com.demo.copilot.taskmanager.domain.entity.Task;
import com.demo.copilot.taskmanager.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    private UserId userId1;
    private UserId userId2;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        userId1 = UserId.generate();
        userId2 = UserId.generate();

        task1 = new Task.Builder()
                .id(TaskId.generate())
                .title("Task 1")
                .description("Description 1")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .category(TaskCategory.DEVELOPMENT)
                .createdBy(userId1)
                .assignedTo(userId1)
                .dueDate(OffsetDateTime.now().plusDays(7))
                .isArchived(false)
                .build();

        task2 = new Task.Builder()
                .id(TaskId.generate())
                .title("Task 2")
                .description("Description 2")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.MEDIUM)
                .category(TaskCategory.TESTING)
                .createdBy(userId2)
                .assignedTo(userId1)
                .dueDate(OffsetDateTime.now().plusDays(3))
                .isArchived(false)
                .build();

        task3 = new Task.Builder()
                .id(TaskId.generate())
                .title("Task 3")
                .description("Description 3")
                .status(TaskStatus.COMPLETED)
                .priority(TaskPriority.LOW)
                .category(TaskCategory.DOCUMENTATION)
                .createdBy(userId1)
                .assignedTo(userId2)
                .dueDate(OffsetDateTime.now().minusDays(1))
                .isArchived(false)
                .build();

        taskRepository.saveAll(List.of(task1, task2, task3));
    }

    @Test
    void findByAssignedTo_ShouldReturnTasksAssignedToUser() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Task> result = taskRepository.findByAssignedTo(userId1, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(Task::getTitle)
                .containsExactlyInAnyOrder("Task 1", "Task 2");
    }

    @Test
    void findByStatus_ShouldReturnTasksWithSpecificStatus() {
        // When
        List<Task> todoTasks = taskRepository.findByStatus(TaskStatus.TODO);
        List<Task> inProgressTasks = taskRepository.findByStatus(TaskStatus.IN_PROGRESS);

        // Then
        assertThat(todoTasks).hasSize(1);
        assertThat(todoTasks.get(0).getTitle()).isEqualTo("Task 1");

        assertThat(inProgressTasks).hasSize(1);
        assertThat(inProgressTasks.get(0).getTitle()).isEqualTo("Task 2");
    }

    @Test
    void findByCreatedBy_ShouldReturnTasksCreatedByUser() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Task> result = taskRepository.findByCreatedBy(userId1, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(Task::getTitle)
                .containsExactlyInAnyOrder("Task 1", "Task 3");
    }

    @Test
    void findTasksAccessibleByUser_ShouldReturnTasksUserCanAccess() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Task> result = taskRepository.findTasksAccessibleByUser(userId1, pageable);

        // Then
        // userId1 created task1 and task3, and is assigned to task1 and task2
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent()).extracting(Task::getTitle)
                .containsExactlyInAnyOrder("Task 1", "Task 2", "Task 3");
    }

    @Test
    void findByAssignedToAndStatus_ShouldReturnFilteredTasks() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Task> result = taskRepository.findByAssignedToAndStatus(userId1, TaskStatus.TODO, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Task 1");
    }

    @Test
    void countByStatus_ShouldReturnCorrectCount() {
        // When
        long todoCount = taskRepository.countByStatus(TaskStatus.TODO);
        long inProgressCount = taskRepository.countByStatus(TaskStatus.IN_PROGRESS);
        long completedCount = taskRepository.countByStatus(TaskStatus.COMPLETED);

        // Then
        assertThat(todoCount).isEqualTo(1);
        assertThat(inProgressCount).isEqualTo(1);
        assertThat(completedCount).isEqualTo(1);
    }

    @Test
    void countByAssignedTo_ShouldReturnCorrectCount() {
        // When
        long user1Count = taskRepository.countByAssignedTo(userId1);
        long user2Count = taskRepository.countByAssignedTo(userId2);

        // Then
        assertThat(user1Count).isEqualTo(2);
        assertThat(user2Count).isEqualTo(1);
    }

    @Test
    void findByIsArchivedFalse_ShouldReturnNonArchivedTasks() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Task> result = taskRepository.findByIsArchivedFalse(pageable);

        // Then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent()).extracting(Task::getIsArchived)
                .allMatch(archived -> !archived);
    }

    @Test
    void save_ShouldPersistTask() {
        // Given
        Task newTask = new Task.Builder()
                .id(TaskId.generate())
                .title("New Task")
                .description("New Description")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.URGENT)
                .category(TaskCategory.WORK)
                .createdBy(userId1)
                .assignedTo(userId1) // Task requires assignedTo to be non-null
                .isArchived(false)
                .build();

        // When
        Task savedTask = taskRepository.save(newTask);

        // Then
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("New Task");
        assertThat(savedTask.getCreatedAt()).isNotNull();

        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getTitle()).isEqualTo("New Task");
    }
}