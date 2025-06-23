# GitHub Copilot Instructions

## 🎯 Project Context

This is a **Spring Boot Task Management System** designed specifically for **GitHub Copilot Coding Agent demonstration**. The project showcases Clean Architecture principles with Domain-Driven Design (DDD) patterns with **intentional architectural violations** for educational purposes.

### Primary Purpose
- **Demo repository** for GitHub Copilot capabilities
- **Educational showcase** of bad-to-good architectural transformation
- **Enterprise-grade** Spring Boot application patterns
- **Real-world scenarios** for architectural decision-making

## 🔧 Context7 Integration

**IMPORTANT**: Always use Context7 for latest library documentation before implementing features.

### Key Libraries to Check
- Spring Boot 3.x patterns and configurations
- Spring Security 6.x JWT implementations
- Spring Data JPA repository patterns
- Spring WebFlux reactive patterns
- Docker & Testcontainers best practices
- OpenAPI/Swagger specifications

## 🏗️ Architecture Guidelines

### Clean Architecture Principles
**CRITICAL**: This project has **intentional architectural violations** for demo purposes.

#### Target Layer Structure
```
Domain Layer (Core) → Application Layer → Infrastructure Layer → Presentation Layer
```

#### Architectural Evolution Status
- Domain entities contain JPA annotations ✅ **RESOLVED** (Issue #4)
- Repository interfaces in infrastructure layer ✅ **RESOLVED** (Issue #4)
- Mixed concerns across layers ✅ **RESOLVED** (Issue #4)
- Use cases not properly implemented ✅ **RESOLVED** (Issue #4)

**Demo Achievement**: Successfully demonstrated GitHub Copilot's capability to refactor toward proper Clean Architecture.

#### Current Architecture State
- **Clean Architecture**: Fully implemented with proper layer separation
- **Domain Purity**: Entities are framework-independent with rich business logic
- **Dependency Inversion**: Domain defines contracts, infrastructure implements
- **Use Case Pattern**: Application layer properly orchestrates business operations

### Domain-Driven Design Patterns
- **Entities**: Rich domain objects with business logic
- **Value Objects**: Immutable objects with validation
- **Aggregates**: Consistency boundaries
- **Repository Contracts**: Abstract data access in domain
- **Domain Services**: Complex business logic
- **Domain Events**: Decoupled communication

## 💻 Coding Standards

### Java & Spring Boot Guidelines
**Note**: Always verify the latest patterns via Context7 before implementation.

#### Entity Design
```java
// ✅ CURRENT: Pure domain entity (Clean Architecture achieved!)
public class Task {
    private TaskId id;
    private String title;
    
    // Business methods
    public void complete() {
        if (this.status == TaskStatus.COMPLETED) {
            throw new IllegalStateException("Task is already completed");
        }
        this.status = TaskStatus.COMPLETED;
        this.completedAt = OffsetDateTime.now();
    }
    
    public boolean isOverdue() {
        return dueDate != null && 
               OffsetDateTime.now().isAfter(dueDate) && 
               status != TaskStatus.COMPLETED;
    }
}

// 🏛️ INFRASTRUCTURE: Separate JPA entity for persistence
@Entity @Table(name = "tasks")
public class TaskJpaEntity {
    @EmbeddedId private TaskId id;
    @Column private String title;
    // Infrastructure concerns isolated from domain
}
```

#### Service Layer Patterns
- **Application Services**: Orchestration only (`@Service`, `@Transactional`)
- **Domain Services**: Business logic without framework dependencies
- **Repository Pattern**: Domain contracts in domain layer, JPA implementations in infrastructure

#### API Design Standards
- Use `@RestController` with `@SecurityRequirement` for OpenAPI
- Return `ResponseEntity<T>` with proper HTTP status codes
- Use `@Valid` for request validation
- Apply `@JsonFormat` for date serialization

#### DTO Design
- Request DTOs with Bean Validation (`@NotBlank`, `@Size`, etc.)
- Response DTOs with proper serialization annotations
- Use Builder pattern for complex objects

## 🧪 Testing Guidelines

### Test Architecture
- **Domain unit tests**: No Spring context, pure business logic testing
- **Integration tests**: Use TestContainers with PostgreSQL 15+
- **Controller tests**: `@WebMvcTest` with `@WithMockUser` for security testing
- **AssertJ**: Preferred assertion library for fluent assertions

## 🎯 Demo-Specific Considerations

### Issue-Based Development
When implementing features for demo issues:

1. **Issue #1 (Microservices)**: Focus on service boundaries and event-driven patterns
2. **Issue #2 (Security)**: Implement enterprise security patterns with JWT
3. **Issue #3 (Analytics)**: Use reactive patterns with WebFlux
4. **Issue #4 (Clean Architecture)**: Fix architectural violations systematically

### Code Quality Standards
- **Builder pattern** for complex object creation
- **Comprehensive JavaDoc** for public APIs
- **Meaningful method names** that express business intent
- **Defensive programming** with validation

### Implementation Guidelines
- **Method-level security** with `@PreAuthorize`
- **Caching** with `@Cacheable` annotations
- **Pagination** for list endpoints
- **Extensive logging** for demo visibility

## 🚀 Development Workflow

### When Adding New Features
1. **Check Context7** for latest library documentation
2. **Start with domain entities** - pure business logic
3. **Define repository contracts** in domain layer
4. **Implement use cases** in application layer
5. **Create infrastructure implementations**
6. **Add presentation layer** (controllers, DTOs)
7. **Write comprehensive tests** at all layers

### Code Generation Preferences
- **Use Builder pattern** for complex objects
- **Implement equals/hashCode** consistently
- **Add comprehensive validation** with custom validators
- **Generate OpenAPI documentation** with detailed examples
- **Create meaningful test data** with realistic scenarios

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
 * ADR: Domain entities are now completely pure following Clean Architecture.
 * JPA concerns have been moved to separate infrastructure entities with
 * proper mapping layers. This demonstrates successful architectural
 * refactoring using GitHub Copilot capabilities.
 * 
 * Status: ✅ RESOLVED (Issue #4)
 */
```

## 🔄 Dependency Management

### Version Strategy
- **Always check Context7** for recommended versions
- **Use Spring Boot's dependency management** for consistency
- **Prefer stable releases** over alpha/beta versions
- **Document version decisions** in pom.xml comments

### Key Dependencies
```xml
<!-- Always verify latest stable versions via Context7 -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>CHECK_CONTEXT7_FOR_LATEST</version>
</parent>
```

---

**Remember**: 
1. This project demonstrates both correct and intentionally flawed implementations to showcase GitHub Copilot's architectural refactoring capabilities.
2. Always consult Context7 for the latest library documentation and best practices before implementation.
3. Consider the demo context when making implementation decisions.
