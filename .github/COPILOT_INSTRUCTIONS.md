# GitHub Copilot Instructions

## 🎯 Project Context

This is a **Spring Boot Task Management System** designed specifically for **GitHub Copilot Coding Agent demonstration**. The project showcases Clean Architecture principles with Domain-Driven Design (DDD) patterns in an enterprise Java environment.

### Primary Purpose
- **Demo repository** for GitHub Copilot capabilities
- **Educational showcase** of Clean Architecture implementation
- **Enterprise-grade** Spring Boot application patterns
- **Real-world scenarios** for architectural decision-making

## 🏗️ Architecture Guidelines

### Clean Architecture Principles
**CRITICAL**: This project follows strict Clean Architecture principles with intentional violations for demo purposes.

#### Layer Structure (Correct Implementation)
```
Domain Layer (Core)
├── Pure business logic
├── No external dependencies
├── Framework-agnostic entities
└── Repository contracts (interfaces only)

Application Layer
├── Use cases and orchestration
├── Application services
├── DTOs for data transfer
└── Depends only on Domain

Infrastructure Layer
├── Database implementations
├── External service integrations
├── Framework-specific code
└── Implements Domain contracts

Presentation Layer
├── REST controllers
├── Request/response handling
├── Input validation
└── Depends on Application layer
```

#### Current State (Intentionally Flawed for Demo)
- Domain entities contain JPA annotations ❌
- Repository interfaces in infrastructure layer ❌
- Mixed concerns across layers ❌
- Use cases not properly implemented ❌

### Domain-Driven Design Patterns
- **Entities**: Rich domain objects with business logic
- **Value Objects**: Immutable objects with validation
- **Aggregates**: Consistency boundaries
- **Repository Contracts**: Abstract data access in domain
- **Domain Services**: Complex business logic
- **Domain Events**: For decoupled communication

## 💻 Coding Standards

### Java & Spring Boot Guidelines

#### Entity Design
```java
// PREFERRED: Pure domain entity (for Clean Architecture)
public class Task {
    private TaskId id;
    private String title;
    
    // Business methods
    public void complete() {
        if (this.status == TaskStatus.COMPLETED) {
            throw new IllegalStateException("Task is already completed");
        }
        this.status = TaskStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
}

// CURRENT: JPA-contaminated entity (for demo purposes)
@Entity @Table(name = "tasks")
public class Task {
    @Id private TaskId id;
    @Column private String title;
    // This violates Clean Architecture but exists for demo
}
```

#### Service Layer Patterns
```java
// Application Service (orchestration only)
@Service
@Transactional
public class TaskApplicationService {
    public TaskResponse createTask(CreateTaskCommand command) {
        // Orchestrate use case
        Task task = createTaskUseCase.execute(command);
        return taskMapper.toResponse(task);
    }
}

// Domain Service (business logic)
public class TaskDomainService {
    public boolean canAssignTask(Task task, User user) {
        // Complex business rules here
        return user.hasRole(UserRole.MANAGER) || 
               task.getProject().hasTeamMember(user);
    }
}
```

#### Repository Pattern
```java
// Domain repository contract (in domain layer)
public interface TaskRepository {
    Task save(Task task);
    Optional<Task> findById(TaskId id);
    List<Task> findByAssignee(UserId userId);
}

// Infrastructure implementation
@Repository
public class JpaTaskRepository implements TaskRepository {
    @Autowired private TaskJpaRepository jpaRepository;
    
    @Override
    public Task save(Task task) {
        TaskJpaEntity entity = mapper.toEntity(task);
        return mapper.toDomain(jpaRepository.save(entity));
    }
}
```

### API Design Standards

#### REST Controller Pattern
```java
@RestController
@RequestMapping("/api/tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {
    
    @PostMapping
    @Operation(summary = "Create new task")
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            Authentication authentication) {
        
        CreateTaskCommand command = mapper.toCommand(request, authentication);
        TaskResponse response = taskService.createTask(command);
        return ResponseEntity.status(201).body(response);
    }
}
```

#### DTO Design
```java
// Request DTO with validation
public class CreateTaskRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;
    
    @Valid
    private TaskPriority priority = TaskPriority.MEDIUM;
}

// Response DTO with proper serialization
public class TaskResponse {
    private UUID id;
    private String title;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dueDate;
}
```

## 🧪 Testing Guidelines

### Test Architecture
```java
// Domain unit test (no Spring context)
class TaskTest {
    @Test
    void shouldCompleteTask() {
        Task task = Task.builder()
            .title("Test Task")
            .status(TaskStatus.IN_PROGRESS)
            .build();
            
        task.complete();
        
        assertThat(task.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(task.getCompletedAt()).isNotNull();
    }
}

// Integration test with TestContainers
@SpringBootTest
@Testcontainers
class TaskRepositoryIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
}

// Controller test with security
@WebMvcTest(TaskController.class)
@WithMockUser(roles = "USER")
class TaskControllerTest {
    @Test
    void shouldCreateTask() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson))
                .andExpect(status().isCreated());
    }
}
```

## 🎯 Demo-Specific Considerations

### Issue-Based Development
When implementing features for demo issues:

1. **Issue #1 (Microservices)**: Focus on service boundaries and event-driven patterns
2. **Issue #2 (Security)**: Implement enterprise security patterns with JWT
3. **Issue #3 (Analytics)**: Use reactive patterns with WebFlux
4. **Issue #4 (Clean Architecture)**: Fix architectural violations systematically

### Code Quality Standards
- **Comprehensive JavaDoc** for public APIs
- **Builder pattern** for complex object creation
- **Immutable value objects** where possible
- **Defensive programming** with null checks and validation
- **Meaningful method names** that express business intent

### Performance Considerations
- **Lazy loading** strategies for JPA entities
- **Caching** with `@Cacheable` annotations
- **Pagination** for list endpoints
- **Database indexing** for query optimization

### Security Implementation
- **Method-level security** with `@PreAuthorize`
- **Input validation** on all public methods
- **SQL injection prevention** with parameterized queries
- **XSS protection** with proper encoding

## 🚀 Development Workflow

### When Adding New Features
1. **Start with domain entities** - pure business logic
2. **Define repository contracts** in domain layer
3. **Implement use cases** in application layer
4. **Create infrastructure implementations**
5. **Add presentation layer** (controllers, DTOs)
6. **Write comprehensive tests** at all layers

### Code Generation Preferences
- **Use Builder pattern** for complex objects
- **Implement equals/hashCode** consistently
- **Add comprehensive validation** with custom validators
- **Generate OpenAPI documentation** with detailed examples
- **Create meaningful test data** with realistic scenarios

### Error Handling
```java
// Domain exceptions
public class TaskDomainException extends RuntimeException {
    public TaskDomainException(String message) {
        super(message);
    }
}

// Application exceptions
public class TaskNotFoundException extends RuntimeException {
    private final TaskId taskId;
    
    public TaskNotFoundException(TaskId taskId) {
        super("Task not found: " + taskId);
        this.taskId = taskId;
    }
}

// Global exception handler
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException ex) {
        return ResponseEntity.status(404)
            .body(ErrorResponse.builder()
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build());
    }
}
```

## 📚 Documentation Standards

### Code Documentation
- **Class-level JavaDoc** explaining purpose and usage
- **Method-level JavaDoc** for public APIs
- **Inline comments** for complex business logic
- **README updates** for new features

### API Documentation
- **OpenAPI annotations** on all endpoints
- **Example requests/responses** in documentation
- **Error code documentation** with descriptions
- **Authentication requirements** clearly specified

## 🎪 Demo Optimization

### When Implementing for Demos
- **Prioritize readability** over clever optimizations
- **Add extensive logging** for demo visibility
- **Include meaningful comments** explaining architectural decisions
- **Create realistic test data** that tells a story
- **Focus on business value** in implementation

### Architecture Decision Records
Document significant architectural decisions in code comments:
```java
/**
 * ADR: We use JPA annotations in domain entities for demo purposes.
 * In a proper Clean Architecture implementation, these would be
 * in separate infrastructure entities with mapping layers.
 * This violation is intentional to showcase Copilot's ability
 * to refactor toward proper Clean Architecture.
 */
```

---

**Remember**: This project demonstrates both correct and intentionally flawed implementations to showcase GitHub Copilot's architectural refactoring capabilities. Always consider the demo context when making implementation decisions.
