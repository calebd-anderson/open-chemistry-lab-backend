# AGENTS.md - Chemlab Backend Development Guide

## Project Overview
**Chemlab** is an interactive periodic table and chemistry education platform built with **Spring Boot 4.0.6**, **Java 21**, and **MongoDB**. It enables users to explore elements, conduct experiments, create flashcards, and take auto-generated quizzes.

### Core Technology Stack
- **Framework**: Spring Boot 4.0.6 (Spring Web, Spring Security, Spring Data MongoDB)
- **Language**: Java 21 LTS (set `JAVA_HOME` to match)
- **Database**: MongoDB 7.0.40 (via Docker or local)
- **Build**: Maven (wrapper: `./mvnw`)
- **Authentication**: JWT (Auth0 library) + BCrypt + Spring Security
- **File Storage**: Azure Blob Storage (prod) / Local filesystem (dev/test)
- **Testing**: JUnit 5 + Mockito + Testcontainers (MongoDB)
- **Secrets**: sops + age encryption for `application-prod.enc.yml`

---

## Architecture Patterns

### Onion Architecture (Package-by-Feature)
The codebase follows **onion/layered architecture**, organized by domain features:

```
controller/api/{domain}/         → HTTP endpoints, request/response handling
    ├── chemistry/               → Element, Reaction APIs
    ├── game/                    → Quiz, Flashcard APIs
    └── user/                    → Auth, Profile APIs
    
service/{domain}/               → Business logic, orchestration
    ├── ElementServiceImpl        → Implements domain.chemistry.ElementService
    ├── ReactionServiceImpl       → Implements domain.chemistry.ReactionService
    └── UserServiceImpl           → User management
    
repository/{domain}/            → Data access layer
    ├── ElementRepository        → Interface (Spring Data MongoDB)
    ├── ElementRepoImpl           → Custom query implementation
    └── ReactionRepository       → MongoDB queries
    
domain/{domain}/                → Service interfaces (contracts)
    └── ServiceInterface<T>      → Generic interface with isValid(T) method
    
model/{domain}/                 → MongoDB entities (@Document)
    ├── Element                  → Chemistry elements
    ├── Reaction                 → Chemical reactions
    └── User                     → User accounts, profiles
    
infrastructure/                 → External integrations
    ├── azure/AzureBlobStorage   → Azure Storage SDK
    ├── pubchem/                 → PubChem API client
    ├── email/                   → Mail service
    └── robohash/                → Avatar generation
```

### Domain Separation
- **Chemistry**: Elements (periodic table), Reactions (compound discovery)
- **Game**: Quiz service, Flashcard management
- **User**: Authentication, Account management, File uploads

**Key Principle**: Each domain has its own controller → service → repository → model path. Controllers import from their domain's service layer, not other domains (except shared utilities).

---

## Critical Developer Workflows

### 1. Local Development Setup
```bash
# Set Java 21
export JAVA_HOME=/path/to/java21  # Or use sdkman/jabba

# Run MongoDB in Docker (required for dev/test)
docker pull mongo:7.0.40-jammy
docker run --name mongodb -p 27017:27017 -d mongo:7.0.40-jammy

# Build and run application
./mvnw clean package          # Full build with tests
./mvnw clean package -DskipTests  # Skip tests (faster)
./mvnw spring-boot:run        # Run app (default: port 8080, dev profile)
./mvnw spring-boot:run -Dapp.profiles=test  # Override active profiles
```

**Note**: Tests require Docker/container runtime for Testcontainers. If containers unavailable, skip tests with `-DskipTests`.

### 2. Testing Strategy
- **Unit Tests** (`src/test/java/{domain}/`): Mock repositories with `@MockitoBean`, use `@SpringBootTest`
- **Integration Tests** (`*IntegrationTest.java`): Real MongoDB via `@Testcontainers` + `@DataMongoTest`
- **Test Fixtures**: Use `@BeforeEach` with mocks; apply `DisplayName` for clarity
- **Assertions**: Prefer AssertJ (`assertThat()`) over JUnit assertions

Example test pattern:
```java
@SpringBootTest
class ElementServiceTest {
    @Autowired private ElementService service;
    @MockitoBean private ElementRepository repo;
    
    @Test
    @DisplayName("should fetch all elements")
    void testGetAll() {
        List<Element> elements = asList(new Element(...));
        when(repo.findAll()).thenReturn(elements);
        assertThat(service.getAllElements()).hasSize(1);
    }
}
```

### 3. Building Docker Image
```bash
./mvnw clean package
docker build -t chemlab .
docker run --rm -p 8080:8080 chemlab
```
**Note**: Dockerfile uses Eclipse Temurin JRE 21 Alpine, runs as non-root `spring:spring` user.

### 4. Secret Management (Production)
```bash
# Decrypt prod secrets (requires age key in ~/.config/sops/age/keys.txt)
sops -d src/main/resources/application-prod.enc.yml > src/main/resources/application-prod.yml

# Encrypt after changes
sops encrypt src/main/resources/application-prod.yml > src/main/resources/application-prod.enc.yml

# Add new recipient
age-keygen -o key.txt                    # Generate key pair
# Add public key to .sops.yaml
sops updatekeys src/main/resources/application-prod.enc.yml
```

---

## Configuration & Profiles

### Profile-Based Configuration
Spring profiles control environment-specific overrides:

| Profile | File | Use Case | In Git? |
|---------|------|----------|---------|
| (default) | `application.yml` | Base config, all profiles | ✅ |
| `dev` | `application-dev.yml` | Development, MongoDB auto-index | ✅ |
| `test` | `application-test.yml` | Unit/integration tests | ✅ |
| `local` | `application-local.yml` | Local dev overrides (user-specific) | ❌ (.gitignore) |
| `prod` | `application-prod.enc.yml` | Production (encrypted) | ✅ |

**Activation**:
```bash
./mvnw spring-boot:run -Dapp.profiles=test
# OR
SPRING_PROFILES_ACTIVE=prod java -jar app.jar
```

### Key Configuration Properties
```yaml
jwt.secret: "${JWT_SECRET:0000000000}"           # Override with env var in prod
spring.data.mongodb.auto-index-creation: false   # Only true in dev/test
web.cors.allowed-origins: []                     # Override per environment
storage.local.path: "storage/uploads"            # For local filesystem fallback
```

---

## HTTP API Conventions

### Automatic `/api` Path Prefix
All routes in `chemlab.controller.api.*` are automatically prefixed with `/api` via `WebConfig.configurePathMatch()`.

Example:
```java
@RestController
@RequestMapping("/elements")
public class ElementController {
    @GetMapping("/list")        // → GET /api/elements/list
    @GetMapping("/symbol/{symbol}")  // → GET /api/elements/symbol/{symbol}
}
```

### Controller Responsibilities (from code comments)
Controllers handle:
- Model/view features
- Endpoint configuration (@RestController, @RequestMapping, @GetMapping, etc.)
- Error handling (exceptions bubble to `ExceptionHandling.java`)
- Request/response transformation

### Error Handling
Domain-specific exceptions live in `exceptions/domain/` (e.g., `FailedToLoadPTException`). Controllers throw these; `ExceptionHandling` class converts them to HTTP responses.

---

## Key Patterns & Conventions

### 1. Service Layer Validation
All services implement `ServiceInterface<T>` with `boolean isValid(T obj)` for input validation:
```java
@Service
public class UserServiceImpl implements UserService, ServiceInterface<User> {
    @Override
    public boolean isValid(User user) {
        return user != null && user.getEmail() != null;
    }
    
    public void register(User user) {
        if (!isValid(user)) throw new ValidationException(...);
    }
}
```

### 2. Repository Pattern
- **Interface** (`ElementRepository extends MongoRepository<Element, String>`): Spring Data queries
- **Custom Implementation** (`ElementRepoImpl`): Complex custom queries via `@Query` or manual operations
- **Injection in Service**: Autowire interface, Spring resolves to implementation

### 3. Lombok Boilerplate Reduction
All entity classes use Lombok annotations:
```java
@Document(collection = "elements")
@Data                      // Generates @Getter, @Setter, @ToString, @EqualsAndHashCode
@AllArgsConstructor        // All-args constructor
@NoArgsConstructor         // No-args constructor
@Builder                   // Builder pattern
public class Element { ... }
```

Service/Controller logging:
```java
@Service
@Slf4j  // Injects SLF4J Logger
public class ElementServiceImpl {
    public void load() {
        log.trace("populating periodic table");  // Visible at TRACE level
    }
}
```

### 4. Authentication & Security
- **JWT Tokens**: Generated by `auth.jwt.*` package, verified in Spring Security filter chain
- **Password Encoding**: `BCryptPasswordEncoder` bean in `ChemistryApplication.java`
- **Security Config**: `auth/config/SecurityConfig.java` defines filter chains, CORS policies
- **Constants**: `SecurityConstants.java` stores JWT-related values

### 5. Dependency Injection
All @Service, @Repository, @Configuration classes use:
```java
@Autowired
private final DependencyType dependency;  // Constructor injection preferred

@Autowired
public ServiceImpl(DependencyType dep) {
    this.dependency = dep;
}
```

**Note**: Using `final` + constructor injection makes dependencies immutable and testable.

### 6. File Upload Storage
`StorageConfig.java` conditionally wires storage implementation:
- **Profile `!prod`** (dev/test): `LocalFileSystemStorage("storage/uploads")`
- **Profile `prod`**: `AzureBlobStorage` (if configured) else fallback to local
- **Inject**: `@Autowired @Qualifier("imageStorageService") ImageStorageService storage`

---

## Integration Points

### External APIs
- **PubChem API** (`infrastructure/pubchem/`): Fetch chemistry data, compound info
- **RoboHash** (`infrastructure/robohash/`): Generate random avatars for users
- **Email** (`infrastructure/email/`): Send notifications via mail service
- **Azure Blob Storage** (`infrastructure/azure/AzureBlobStorage`): User file uploads (prod)

### Intra-Service Communication
Services in different domains **do not import each other directly**. Use:
- **Event system** (if implemented, check `infrastructure/events/`)
- **REST calls** via `RestTemplate` bean (defined in `ChemistryApplication.java`)
- **Shared DTOs** in common model package

---

## Common Development Tasks

### Adding a New API Endpoint
1. **Create/update Controller** in `controller/api/{domain}/SomeController.java`
2. **Add Service method** in `service/{domain}/SomeServiceImpl.java`
3. **Add Repository query** in `repository/{domain}/SomeRepository.java` (if DB access needed)
4. **Update domain interface** in `domain/{domain}/SomeService.java`
5. **Add tests** in `src/test/java/chemlab/{controllers,services,repository}/{domain}/`
6. **Run tests**: `./mvnw test`

### Modifying Database Model
1. Update entity in `model/{domain}/SomeEntity.java` with Lombok annotations
2. MongoDB auto-creates collections; index creation controlled by profile property `spring.data.mongodb.auto-index-creation`
3. Add custom queries to repository if needed
4. Update service layer to use new fields
5. Run `./mvnw clean package` to validate changes

### Debugging
- **Enable trace logging** in `application-local.yml`: `logging.level.chemlab: TRACE`
- **MongoDB shell**: `docker exec -it mongodb mongosh`
- **Spring Boot actuator**: `localhost:8080/actuator/health` (enabled by default)
- **Test with breakpoints**: IDEs (IntelliJ) support debugging via `./mvnw spring-boot:run` with remote debugger

---

## Code Style & Naming Conventions

- **Package names**: Lowercase, hierarchical by domain (`chemlab.{layer}.{domain}`)
- **Class names**: PascalCase; service impl suffix `*ServiceImpl`, controller suffix usually omitted (`ElementController`)
- **Method names**: camelCase; getters/setters auto-generated by Lombok
- **Constants**: `UPPER_SNAKE_CASE` in dedicated `*Constants.java` files
- **Logging**: Use `@Slf4j` and `log.{trace,debug,info,warn,error}(msg)` with context

### Comment Conventions
Controllers include inline comments describing responsibility:
```java
// the controller class has model view features, end-point config, and error handling
@RestController
public class ElementController { ... }
```

---

## Version Control & CI/CD Tips

- **Branch naming**: Feature branches typically `feature/description`
- **Commit messages**: Describe what changed and why
- **Before pushing**: Run `./mvnw clean package` to ensure tests pass
- **Pull request checks**: All tests must pass; code review required
- **Docker image**: Built and pushed during CI/CD pipeline (see `.github/workflows/` if available)

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Maven build fails with Java version mismatch | Ensure `JAVA_HOME` points to Java 21; verify `./mvnw --version` |
| Tests fail with "Cannot connect to MongoDB" | Start MongoDB Docker container; Testcontainers will pull image if needed |
| CDS warning during test run | Already handled by pom.xml surefire config (`-Xshare:off`) |
| CORS errors from frontend | Check `application-{profile}.yml` `web.cors.allowed-origins` |
| JWT auth failing | Verify `JWT_SECRET` env var is set; check token expiry in `auth/jwt/` |
| File uploads fail in prod | Check Azure Blob Storage credentials in `application-prod.yml` |

---

## References

- **Spring Boot Docs**: https://docs.spring.io/spring-boot/
- **Spring Data MongoDB**: https://docs.spring.io/spring-data/mongodb/docs/
- **PubChem API**: https://pubchem.ncbi.nlm.nih.gov/docs/pug-rest-tutorial
- **Onion Architecture**: https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/
- **sops**: https://getsops.io/
- **age encryption**: https://github.com/FiloSottile/age

