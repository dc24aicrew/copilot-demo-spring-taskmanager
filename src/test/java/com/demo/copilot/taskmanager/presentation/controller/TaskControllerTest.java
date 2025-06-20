package com.demo.copilot.taskmanager.presentation.controller;

import com.demo.copilot.taskmanager.application.dto.task.CreateTaskRequest;
import com.demo.copilot.taskmanager.application.dto.task.TaskResponse;
import com.demo.copilot.taskmanager.application.dto.task.TaskSummaryResponse;
import com.demo.copilot.taskmanager.application.service.TaskService;
import com.demo.copilot.taskmanager.application.mapper.TaskMapper;
import com.demo.copilot.taskmanager.domain.valueobject.TaskCategory;
import com.demo.copilot.taskmanager.domain.valueobject.TaskPriority;
import com.demo.copilot.taskmanager.domain.valueobject.TaskStatus;
import com.demo.copilot.taskmanager.infrastructure.repository.TaskRepository;
import com.demo.copilot.taskmanager.infrastructure.security.JwtService;
import com.demo.copilot.taskmanager.test.util.TestPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private JwtService jwtService;

    @MockBean 
    private TaskRepository taskRepository;
    
    @MockBean
    private TaskMapper taskMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "550e8400-e29b-41d4-a716-446655440000", roles = "USER")
    void createTask_ShouldReturnCreatedTask() throws Exception {
        // Given
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        request.setPriority(TaskPriority.HIGH);
        request.setCategory(TaskCategory.DEVELOPMENT);
        request.setDueDate(OffsetDateTime.now().plusDays(7));

        TaskResponse response = new TaskResponse();
        response.setId(UUID.randomUUID());
        response.setTitle("Test Task");
        response.setDescription("Test Description");
        response.setPriority(TaskPriority.HIGH);
        response.setCategory(TaskCategory.DEVELOPMENT);
        response.setStatus(TaskStatus.TODO);

        when(taskService.createTask(any(CreateTaskRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/tasks")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("TODO"));

        verify(taskService).createTask(any(CreateTaskRequest.class));
    }

    @Test
    @WithMockUser(username = "550e8400-e29b-41d4-a716-446655440000", roles = "USER")
    void createTask_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        CreateTaskRequest request = new CreateTaskRequest();
        // Missing required title and priority

        // When & Then
        mockMvc.perform(post("/tasks")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "550e8400-e29b-41d4-a716-446655440000", roles = "USER")
    void getTaskById_ShouldReturnTask() throws Exception {
        // Given
        UUID taskId = UUID.randomUUID();
        TaskResponse response = new TaskResponse();
        response.setId(taskId);
        response.setTitle("Test Task");
        response.setStatus(TaskStatus.TODO);

        when(taskService.getTaskById(eq(taskId))).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.status").value("TODO"));

        verify(taskService).getTaskById(taskId);
    }

    @Test
    @WithMockUser(username = "550e8400-e29b-41d4-a716-446655440000", roles = "USER")
    void getAllTasks_ShouldReturnPageOfTasks() throws Exception {
        // Given
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OffsetDateTime now = OffsetDateTime.now();
        
        TaskSummaryResponse task1 = new TaskSummaryResponse();
        task1.setId(UUID.randomUUID());
        task1.setTitle("Task 1");
        task1.setStatus(TaskStatus.TODO);
        task1.setPriority(TaskPriority.HIGH);
        task1.setCategory(TaskCategory.DEVELOPMENT);
        task1.setDueDate(now.plusDays(7));
        task1.setAssignedTo(userId);
        task1.setCreatedBy(userId);
        task1.setCreatedAt(now);
        task1.setUpdatedAt(now);

        TaskSummaryResponse task2 = new TaskSummaryResponse();
        task2.setId(UUID.randomUUID());
        task2.setTitle("Task 2");
        task2.setStatus(TaskStatus.IN_PROGRESS);
        task2.setPriority(TaskPriority.MEDIUM);
        task2.setCategory(TaskCategory.TESTING);
        task2.setDueDate(now.plusDays(14));
        task2.setAssignedTo(userId);
        task2.setCreatedBy(userId);
        task2.setCreatedAt(now);
        task2.setUpdatedAt(now);

        Page<TaskSummaryResponse> taskPage = new TestPage<>(List.of(task1, task2));

        when(taskService.getAllTasks(any())).thenReturn(taskPage);

        // When & Then
        mockMvc.perform(get("/tasks")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Task 1"))
                .andExpect(jsonPath("$.content[1].title").value("Task 2"))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(taskService).getAllTasks(any());
    }

    @Test
    @WithMockUser(username = "550e8400-e29b-41d4-a716-446655440000", roles = "USER")
    void deleteTask_ShouldReturnNoContent() throws Exception {
        // Given
        UUID taskId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(delete("/tasks/{id}", taskId)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(taskId);
    }

    @Test
    void createTask_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // Given
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Test Task");
        request.setPriority(TaskPriority.HIGH);

        // When & Then
        mockMvc.perform(post("/tasks")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "550e8400-e29b-41d4-a716-446655440000", roles = "USER")
    void getMyAssignedTasks_ShouldReturnAssignedTasks() throws Exception {
        // Given
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OffsetDateTime now = OffsetDateTime.now();
        
        TaskSummaryResponse task = new TaskSummaryResponse();
        task.setId(UUID.randomUUID());
        task.setTitle("Assigned Task");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.HIGH);
        task.setCategory(TaskCategory.DEVELOPMENT);
        task.setDueDate(now.plusDays(7));
        task.setAssignedTo(userId);
        task.setCreatedBy(userId);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        Page<TaskSummaryResponse> taskPage = new TestPage<>(List.of(task));

        when(taskService.getMyAssignedTasks(any())).thenReturn(taskPage);

        // When & Then
        mockMvc.perform(get("/tasks/my/assigned"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Assigned Task"));

        verify(taskService).getMyAssignedTasks(any());
    }

    @Test
    @WithMockUser(username = "550e8400-e29b-41d4-a716-446655440000", roles = "USER")
    void getMyCreatedTasks_ShouldReturnCreatedTasks() throws Exception {
        // Given
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        OffsetDateTime now = OffsetDateTime.now();
        
        TaskSummaryResponse task = new TaskSummaryResponse();
        task.setId(UUID.randomUUID());
        task.setTitle("Created Task");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.MEDIUM);
        task.setCategory(TaskCategory.DEVELOPMENT);
        task.setDueDate(now.plusDays(5));
        task.setAssignedTo(userId);
        task.setCreatedBy(userId);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        Page<TaskSummaryResponse> taskPage = new TestPage<>(List.of(task));

        when(taskService.getMyCreatedTasks(any())).thenReturn(taskPage);

        // When & Then
        mockMvc.perform(get("/tasks/my/created"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Created Task"));

        verify(taskService).getMyCreatedTasks(any());
    }
}