package com.demo.copilot.taskmanager.application.service;

import com.demo.copilot.taskmanager.application.dto.task.CreateTaskRequest;
import com.demo.copilot.taskmanager.application.dto.task.TaskResponse;
import com.demo.copilot.taskmanager.application.dto.task.TaskSummaryResponse;
import com.demo.copilot.taskmanager.application.exception.TaskNotFoundException;
import com.demo.copilot.taskmanager.application.mapper.TaskMapper;
import com.demo.copilot.taskmanager.domain.entity.Task;
import com.demo.copilot.taskmanager.domain.valueobject.*;
import com.demo.copilot.taskmanager.domain.repository.TaskRepositoryContract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TaskServiceTest {

    @Mock
    private TaskRepositoryContract taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;
    private TaskResponse sampleTaskResponse;
    private CreateTaskRequest createTaskRequest;
    private UserId currentUserId;

    @BeforeEach
    void setUp() {
        currentUserId = UserId.of(UUID.randomUUID());
        
        // Setup security context
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentUserId.getValue().toString());
        
        @SuppressWarnings("unchecked")
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        doReturn(authorities).when(authentication).getAuthorities();

        // Create sample task
        sampleTask = new Task.Builder()
                .id(TaskId.generate())
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .category(TaskCategory.DEVELOPMENT)
                .createdBy(currentUserId)
                .assignedTo(currentUserId) // Task requires assignedTo to be non-null
                .dueDate(OffsetDateTime.now().plusDays(7))
                .isArchived(false)
                .build();

        // Create sample response
        sampleTaskResponse = new TaskResponse();
        sampleTaskResponse.setId(sampleTask.getId().getValue());
        sampleTaskResponse.setTitle(sampleTask.getTitle());
        sampleTaskResponse.setDescription(sampleTask.getDescription());
        sampleTaskResponse.setStatus(sampleTask.getStatus());
        sampleTaskResponse.setPriority(sampleTask.getPriority());
        sampleTaskResponse.setCategory(sampleTask.getCategory());
        sampleTaskResponse.setCreatedBy(sampleTask.getCreatedBy().getValue());

        // Create sample request
        createTaskRequest = new CreateTaskRequest();
        createTaskRequest.setTitle("New Task");
        createTaskRequest.setDescription("New Description");
        createTaskRequest.setPriority(TaskPriority.MEDIUM);
        createTaskRequest.setCategory(TaskCategory.WORK);
        createTaskRequest.setDueDate(OffsetDateTime.now().plusDays(5));
    }

    @Test
    void createTask_ShouldCreateTaskSuccessfully() {
        // Given
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);
        when(taskMapper.toResponse(any(Task.class))).thenReturn(sampleTaskResponse);

        // When
        TaskResponse result = taskService.createTask(createTaskRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(sampleTaskResponse.getTitle());
        assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(result.getCreatedBy()).isEqualTo(currentUserId.getValue());
        
        verify(taskRepository).save(any(Task.class));
        verify(taskMapper).toResponse(any(Task.class));
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() {
        // Given
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(any(TaskId.class))).thenReturn(Optional.of(sampleTask));
        when(taskMapper.toResponse(any(Task.class))).thenReturn(sampleTaskResponse);

        // When
        TaskResponse result = taskService.getTaskById(taskId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(sampleTaskResponse);
        
        verify(taskRepository).findById(any(TaskId.class));
        verify(taskMapper).toResponse(sampleTask);
    }

    @Test
    void getTaskById_WhenTaskNotExists_ShouldThrowException() {
        // Given
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(any(TaskId.class))).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.getTaskById(taskId))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task not found with id:");
        
        verify(taskRepository).findById(any(TaskId.class));
        verify(taskMapper, never()).toResponse(any(Task.class));
    }

    @Test
    void getAllTasks_AsRegularUser_ShouldReturnAccessibleTasks() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(List.of(sampleTask));
        
        when(taskRepository.findTasksAccessibleByUser(any(UserId.class), any(Pageable.class)))
                .thenReturn(taskPage);
        when(taskMapper.toSummaryResponse(any(Task.class))).thenReturn(mock(TaskSummaryResponse.class));

        // When
        taskService.getAllTasks(pageable);

        // Then
        verify(taskRepository).findTasksAccessibleByUser(any(UserId.class), any(Pageable.class));
        verify(taskRepository, never()).findByIsArchivedFalse(pageable);
    }

    @Test
    void getAllTasks_AsAdmin_ShouldReturnAllTasks() {
        // Given
        @SuppressWarnings("unchecked")
        Collection<GrantedAuthority> adminAuthorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(adminAuthorities).when(authentication).getAuthorities();
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(List.of(sampleTask));
        
        when(taskRepository.findByIsArchivedFalse(any(Pageable.class))).thenReturn(taskPage);
        when(taskMapper.toSummaryResponse(any(Task.class))).thenReturn(mock(TaskSummaryResponse.class));

        // When
        taskService.getAllTasks(pageable);

        // Then
        verify(taskRepository).findByIsArchivedFalse(any(Pageable.class));
        verify(taskRepository, never()).findTasksAccessibleByUser(any(UserId.class), any(Pageable.class));
    }

    @Test
    void deleteTask_WhenUserIsTaskCreator_ShouldDeleteTask() {
        // Given
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(any(TaskId.class))).thenReturn(Optional.of(sampleTask));

        // When
        taskService.deleteTask(taskId);

        // Then
        verify(taskRepository).findById(any(TaskId.class));
        verify(taskRepository).delete(sampleTask);
    }

    @Test
    void deleteTask_WhenUserIsNotTaskCreator_ShouldThrowException() {
        // Given
        UUID taskId = UUID.randomUUID();
        UserId otherUserId = UserId.of(UUID.randomUUID());
        
        Task taskByOtherUser = new Task.Builder()
                .id(TaskId.generate())
                .title("Other's Task")
                .description("Description")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.LOW)
                .category(TaskCategory.PERSONAL)
                .createdBy(otherUserId)
                .assignedTo(otherUserId) // Task requires assignedTo to be non-null
                .isArchived(false)
                .build();
        
        when(taskRepository.findById(any(TaskId.class))).thenReturn(Optional.of(taskByOtherUser));

        // When & Then
        assertThatThrownBy(() -> taskService.deleteTask(taskId))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task not found with id:");
        
        verify(taskRepository).findById(any(TaskId.class));
        verify(taskRepository, never()).delete(any(Task.class));
    }
}