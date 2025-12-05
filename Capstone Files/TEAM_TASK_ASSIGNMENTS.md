# Team Task Assignments

**Project:** Software Project Management Plan Evaluator  
**Team:** Team 02  
**Sprint Duration:** December 5 - December 12, 2025 (1 Week)  
**Presentation Date:** December 12, 2025

---

## Team Overview

| Member | Branch | Module | Role |
|--------|--------|--------|------|
| **Lawas, Jose Raphael** | `feature/Lawas` | Module 1: Basic Login and Security System | Team Leader / Repository Owner |
| **Lapure, Jessie Noel** | `feature/Lapure` | Module 2: Role-Based User Interface Transactions | Frontend + Backend |
| **Laborada, John Joseph** | `feature/Laborada` | Module 3: Automated Parser Module | Backend / AI Integration |
| **Pepito, John Patrick** | `feature/Pepito` | Module 4: Generate Score & Feedback | Backend / Scoring Logic |
| **Verano, Joel** | `feature/Verano` | Non-Functional Requirements | DevOps / Testing / Security |

---

## Git Workflow

```bash
# Each member should:
git checkout main
git pull origin main
git checkout -b feature/<YourLastName>

# After completing work:
git add .
git commit -m "feat(<module>): <description>"
git push -u origin feature/<YourLastName>

# Create Pull Request to main branch for review
```

---

## Module 1: Basic Login and Security System
**Branch:** `feature/Lawas`  
**Assigned To:** Lawas, Jose Raphael (Leader)  
**Status:** ✅ Mostly Complete

### Current Implementation
- ✅ User Registration with role selection (STUDENT, PROFESSOR, PM)
- ✅ User Login with JWT token generation
- ✅ Password Encryption (BCrypt)
- ✅ Session Management via JWT
- ✅ Logout Functionality (frontend token removal)
- ✅ Role-based Access Control

### Remaining Tasks

| Task | Priority | File(s) | Description |
|------|----------|---------|-------------|
| Password Reset (Forgot Password) | HIGH | `AuthController.java`, `UserService.java` | Add endpoint `POST /api/auth/forgot-password` and `POST /api/auth/reset-password` |
| Email Service for Password Reset | MEDIUM | Create `EmailService.java` | Send password reset link via email (can use JavaMailSender) |
| Login Attempt Logging | MEDIUM | `AuditLogRepository.java`, `AuthController.java` | Log all login attempts (success/failure) for security auditing |
| Account Lockout | LOW | `UserService.java` | Lock account after 5 failed login attempts |
| Password Strength Validation | LOW | `RegisterRequest.java` | Add regex validation for strong passwords |

### API Endpoints to Add

```java
POST /api/auth/forgot-password
POST /api/auth/reset-password
POST /api/auth/change-password
```

### DTOs to Create
- `ForgotPasswordRequest.java` - email field
- `ResetPasswordRequest.java` - token, newPassword fields
- `ChangePasswordRequest.java` - oldPassword, newPassword fields

### Checklist
- [x] Register/login endpoints validated with happy-path and invalid credentials
- [x] Password hashing in place (BCrypt) and verified in DB
- [x] JWT issued with expiry, refresh or re-login flow decided
- [x] Role-based access control enforced on protected endpoints
- [ ] Password reset (token flow or change-password with old password) working
- [x] CORS configured for frontend origins
- [ ] Basic rate-limiting or brute-force mitigation for auth
- [ ] Auth unit/integration tests green
- [ ] Swagger or README notes updated for auth routes

---

## Module 2: Role-Based User Interface Transactions
**Branch:** `feature/Lapure`  
**Assigned To:** Lapure, Jessie Noel  
**Status:** 🔴 Not Started

### Use Cases (2.1 - 2.10)

#### Student Role (2.1 - 2.5)

| UC# | Use Case | Backend | Frontend | Priority |
|-----|----------|---------|----------|----------|
| 2.1 | File Upload | ✅ Exists | ❌ Build | HIGH |
| 2.2 | File Edit/Replace | ⚠️ Add endpoint | ❌ Build | HIGH |
| 2.3 | File Removal | ✅ Exists | ❌ Build | MEDIUM |
| 2.4 | View Feedback | ✅ Exists | ❌ Build | HIGH |
| 2.5 | Task Tracking | ✅ Exists | ❌ Build | MEDIUM |

#### Professor Role (2.6 - 2.10)

| UC# | Use Case | Backend | Frontend | Priority |
|-----|----------|---------|----------|----------|
| 2.6 | Task Creation | ✅ Exists | ❌ Build | HIGH |
| 2.7 | Submission Tracker | ⚠️ Add endpoint | ❌ Build | HIGH |
| 2.8 | Override AI Results | ⚠️ Add endpoint | ❌ Build | HIGH |
| 2.9 | Update Tasks | ✅ Exists | ❌ Build | MEDIUM |
| 2.10 | Monitor Student Progress | ⚠️ Add endpoint | ❌ Build | MEDIUM |

### Backend Tasks

| Task | File | Description |
|------|------|-------------|
| File Replace Endpoint | `DocumentController.java` | `PUT /api/documents/{id}/replace` - Replace uploaded file |
| All Submissions Endpoint | `DocumentController.java` | `GET /api/documents/all-submissions` - Professor views all student docs |
| Override Score Endpoint | `DocumentController.java` | `PUT /api/documents/{id}/override-score` - Direct score modification |
| Student Progress Endpoint | `ReportingController.java` | `GET /api/reports/student-progress/{studentId}` - Task completion stats |

### Backend Code Examples

**File Replace Endpoint:**
```java
@PutMapping("/{documentId}/replace")
public ResponseEntity<?> replaceDocument(
    @PathVariable Long documentId,
    @RequestParam("file") MultipartFile file) {
    // Implementation: Delete old file, upload new one, keep metadata
}
```

**Override Score Endpoint:**
```java
@PutMapping("/{documentId}/override-score")
public ResponseEntity<?> overrideScore(
    @PathVariable Long documentId,
    @RequestBody Map<String, Double> scoreData) {
    // scoreData: { "overallScore": 85.5 }
    // Update ComplianceScore.overallScore directly
}
```

**All Submissions Endpoint:**
```java
@GetMapping("/all-submissions")
public ResponseEntity<?> getAllSubmissions(
    @RequestParam(required = false) String status,
    @RequestParam(required = false) Long studentId) {
    // Return list of SPMPDocument with filters
}
```

### Frontend Tasks

#### Design System Setup
- [ ] Update `tailwind.config.js` with color palette (violet primary, dark theme)
- [ ] Add Inter font to `index.html`
- [ ] Create animation keyframes in `index.css` (fadeIn, slideIn, pulse)
- [ ] Build reusable components: `Button.jsx`, `Card.jsx`, `Badge.jsx`, `Modal.jsx`, `FileDropzone.jsx`

#### Student Dashboard Components
- [ ] `DocumentUpload.jsx` - Drag-drop file upload with progress bar
- [ ] `DocumentList.jsx` - Table/grid of uploaded documents with status badges
- [ ] `EvaluationResults.jsx` - Score gauge, section breakdown, recommendations
- [ ] `TaskTracker.jsx` - Timeline view of assigned tasks with completion status

#### Professor Dashboard Components
- [ ] `SubmissionTracker.jsx` - Filterable table of all student submissions
- [ ] `TaskManager.jsx` - Create/edit/delete task forms
- [ ] `ScoreOverride.jsx` - Modal for direct score modification
- [ ] `StudentProgress.jsx` - Progress bars and analytics for student performance

#### API Service Updates
- [ ] Extend `apiService.js` with `documentAPI` (upload, list, evaluate, replace, delete)
- [ ] Add `taskAPI` (create, getMyTasks, update, complete, delete)
- [ ] Add `reportAPI` (getStatistics, getStudentPerformance, getTrends)

#### Dashboard Integration
- [ ] Update `Dashboard.jsx` to render role-specific components
- [ ] Add loading states, error handling, toast notifications
- [ ] Implement responsive design for mobile/tablet

### OpenRouter AI Integration (Local Branch Only)

**Configuration:**
```properties
# application-local.properties
openrouter.api.key=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
openrouter.api.url=https://openrouter.ai/api/v1/chat/completions
openrouter.model=mistralai/mistral-7b-instruct:free
```

**Service Implementation:**
- [ ] Create `OpenRouterEvaluationService.java`
- [ ] Refactor `ComplianceEvaluationService` to use AI for section detection
- [ ] Add semantic analysis for content quality (not just keyword matching)

### Checklist
- [ ] API service covers documents (upload/list/evaluate/replace/delete), tasks (CRUD), reports (stats/progress)
- [ ] Student dashboard: upload (drag-drop), list with status, evaluate action, delete, view feedback
- [ ] Professor dashboard: submission tracker table with filters, task create/update/delete, score override modal
- [ ] File replace endpoint wired on backend and used in UI
- [ ] Progress/analytics cards for student progress (per SRS Module 2.10)
- [ ] Role-guarded routes/components; unauthorized access blocked in UI and via API
- [ ] Error/loading/toast states for all async flows
- [ ] Responsive layout and accessibility checks for key flows
- [ ] Integration tests (happy path upload→evaluate; professor override flow)

---

## Module 3: Automated Parser Module
**Branch:** `feature/Laborada`  
**Assigned To:** Laborada, John Joseph  
**Status:** ✅ Partially Complete

### Current Implementation
- ✅ PDF text extraction using Apache PDFBox
- ✅ DOCX text extraction using Apache POI
- ✅ Keyword-based section detection for IEEE 1058
- ✅ Integration with `ComplianceEvaluationService`

### Remaining Tasks

| Task | Priority | File(s) | Description |
|------|----------|---------|-------------|
| Enhanced Error Handling | HIGH | `DocumentParser.java` | Better error messages for corrupt/unsupported files |
| Performance Optimization | MEDIUM | `DocumentParser.java` | Ensure extraction <10s for 50-page documents |
| Security Hardening | HIGH | `DocumentParser.java`, `DocumentController.java` | Sanitize inputs, prevent XXE attacks, validate file types |
| AI-Powered Section Detection | HIGH | Create `OpenRouterEvaluationService.java` | Replace keyword matching with semantic AI analysis |
| Metadata Extraction | LOW | `DocumentParser.java` | Extract author, creation date, word count |

### AI Integration (OpenRouter)

**Create `OpenRouterEvaluationService.java`:**
```java
@Service
public class OpenRouterEvaluationService {
    
    @Value("${openrouter.api.key}")
    private String apiKey;
    
    @Value("${openrouter.api.url}")
    private String apiUrl;
    
    @Value("${openrouter.model}")
    private String model;
    
    public List<String> detectSections(String documentContent) {
        // Call OpenRouter API with prompt:
        // "Analyze this SPMP document and identify which IEEE 1058 sections are present..."
        // Return list of detected sections
    }
    
    public String generateFeedback(String sectionName, String content) {
        // Generate contextual feedback for each section
    }
}
```

**Refactor `ComplianceEvaluationService.java`:**
- Replace keyword matching with AI-based detection
- Use semantic analysis for content quality assessment
- Generate more detailed, context-aware recommendations

### Checklist
- [x] PDF and DOCX extraction verified with sample files (normal and edge cases)
- [x] Keyword matching covers all IEEE 1058 sections per constants
- [ ] Parser returns meaningful errors for corrupt/unsupported files
- [ ] Performance: extraction <10s for 50-page doc
- [ ] Unit tests for extraction and keyword detection pass
- [x] Service integrates parser output into evaluation flow
- [ ] Security: input sanitized; no temp file leaks
- [ ] AI-powered section detection implemented (OpenRouter integration)

---

## Module 4: Generate Score & Feedback
**Branch:** `feature/Pepito`  
**Assigned To:** Pepito, John Patrick  
**Status:** ✅ Partially Complete

### Current Implementation
- ✅ Scoring formula with structure (30%) + completeness (70%) weights
- ✅ Section-by-section analysis stored in `SectionAnalysis` entity
- ✅ Compliance score calculation and persistence
- ✅ Basic feedback generation for missing/present sections
- ✅ API endpoints for retrieving scores and reports

### Remaining Tasks

| Task | Priority | File(s) | Description |
|------|----------|---------|-------------|
| Configurable Weights | HIGH | `IEEE1058StandardConstants.java`, `application.properties` | Make structure/completeness weights configurable via properties |
| Enhanced Feedback | HIGH | `ComplianceEvaluationService.java` | More detailed, actionable recommendations per section |
| Score Override Support | HIGH | `DocumentController.java`, `ComplianceScore.java` | Allow professors to override scores with justification notes |
| Re-evaluation Logic | MEDIUM | `SPMPDocumentService.java` | Handle re-evaluation of already evaluated documents |
| Score History Tracking | LOW | Create `ComplianceScoreHistory.java` | Track score changes over time |
| Export Reports | LOW | Create `ReportExportService.java` | Export compliance reports as PDF/Excel |

### Enhanced Feedback Examples

**Current (Basic):**
```
"Section 'Risk Management' not found in document."
"Add the 'Risk Management' section to comply with IEEE 1058 standard."
```

**Enhanced (Detailed):**
```
"Section 'Risk Management' not found in document."
"The Risk Management section should include:
- Identification of potential risks
- Risk probability and impact assessment
- Mitigation strategies for each risk
- Risk monitoring and control procedures
Refer to IEEE 1058-1998 Section 5.6 for detailed requirements."
```

### Configurable Weights Implementation

**application.properties:**
```properties
# Scoring Configuration
scoring.structure.weight=0.30
scoring.completeness.weight=0.70
scoring.minimum.compliance.threshold=0.80
```

**IEEE1058StandardConstants.java:**
```java
@Component
public class IEEE1058StandardConstants {
    
    @Value("${scoring.structure.weight:0.30}")
    public static double STRUCTURE_WEIGHT;
    
    @Value("${scoring.completeness.weight:0.70}")
    public static double COMPLETENESS_WEIGHT;
    
    @Value("${scoring.minimum.compliance.threshold:0.80}")
    public static double MINIMUM_COMPLIANCE_THRESHOLD;
}
```

### Score Override Implementation

**Add to `ComplianceScore.java`:**
```java
@Column(name = "override_score")
private Double overrideScore;

@Column(name = "override_notes", length = 1000)
private String overrideNotes;

@Column(name = "overridden_by")
private Long overriddenBy; // Professor user ID

@Column(name = "overridden_at")
private LocalDateTime overriddenAt;
```

**Add to `DocumentController.java`:**
```java
@PutMapping("/{documentId}/override-score")
public ResponseEntity<?> overrideScore(
    @PathVariable Long documentId,
    @RequestBody ScoreOverrideDTO overrideDTO) {
    
    // Verify user is PROFESSOR or PM
    // Update ComplianceScore with override values
    // Log the override action in AuditLog
}
```

### Checklist
- [x] Scoring formula implemented with configurable weights (structure/completeness)
- [x] Section analyses persisted and retrievable via DTOs
- [ ] Feedback text clear and actionable for missing/present sections
- [x] API returns overall score, per-section scores, compliance flag
- [ ] Thresholds (e.g., min compliance) configurable via properties
- [ ] Unit/integration tests for scoring logic and API responses
- [ ] Handles re-evaluation idempotently (updates existing scores)
- [ ] Score override functionality with audit trail

---

## Non-Functional Requirements
**Branch:** `feature/Verano`  
**Assigned To:** Verano, Joel  
**Status:** 🔴 Not Started

### Tasks Overview

| Category | Priority | Description |
|----------|----------|-------------|
| Performance Testing | HIGH | Ensure endpoints respond <3s under load |
| Logging & Monitoring | HIGH | Structured logging with log levels |
| Input Validation | HIGH | DTO validation and global exception handling |
| Security Hardening | HIGH | Prevent SQL injection, XSS, CSRF |
| Rate Limiting | MEDIUM | Throttle auth and upload endpoints |
| Health Checks | MEDIUM | Expose /actuator/health endpoints |
| Documentation | MEDIUM | API docs, deployment guide, runbook |
| Load Testing | LOW | Basic performance profiling |

### Performance Requirements

**Target Metrics:**
- Login/Register: <2s response time
- Document Upload: <5s for files up to 50MB
- Document Evaluation: <10s for 50-page documents
- API Endpoints: <3s response time (95th percentile)
- Database Queries: <500ms for complex queries

**Implementation:**
- [ ] Use JMeter or Gatling for load testing
- [ ] Profile slow endpoints with Spring Boot Actuator
- [ ] Add database indexing for frequently queried fields
- [ ] Implement caching for static data (e.g., user roles)

### Logging & Monitoring

**Logging Strategy:**
```java
// Use SLF4J with Logback
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    public User registerUser(...) {
        log.info("User registration attempt for username: {}", username);
        try {
            // Registration logic
            log.info("User registered successfully: {}", user.getId());
        } catch (Exception e) {
            log.error("User registration failed for username: {}", username, e);
        }
    }
}
```

**Monitoring Setup:**
- [ ] Enable Spring Boot Actuator
- [ ] Expose metrics endpoint `/actuator/metrics`
- [ ] Add health check endpoint `/actuator/health`
- [ ] Configure Logback for file rotation (logs/app.log)

**application.properties:**
```properties
# Actuator Configuration
management.endpoints.web.exposure.include=health,metrics,info
management.endpoint.health.show-details=when-authorized

# Logging Configuration
logging.file.name=logs/spmpevaluator.log
logging.file.max-size=10MB
logging.file.max-history=30
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

### Input Validation & Error Handling

**Global Exception Handler:**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage(), 400));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericError(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity.status(500).body(new ErrorResponse("Internal server error", 500));
    }
}
```

**DTO Validation Example:**
```java
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    private String username;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$", 
             message = "Password must be at least 8 characters with letters and numbers")
    private String password;
}
```

### Security Hardening

**Security Checklist:**
- [ ] Enable HTTPS/TLS in production
- [ ] Add CORS configuration for allowed origins
- [ ] Implement CSRF protection for state-changing operations
- [ ] Use parameterized queries to prevent SQL injection
- [ ] Sanitize user inputs to prevent XSS attacks
- [ ] Add Content Security Policy (CSP) headers
- [ ] Implement rate limiting on auth endpoints
- [ ] Add security headers (X-Frame-Options, X-Content-Type-Options)

**Security Configuration:**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable() // Use token-based auth instead
            .headers()
                .contentSecurityPolicy("default-src 'self'")
                .and()
                .frameOptions().deny()
                .and()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

### Rate Limiting

**Bucket4j Implementation:**
```xml
<!-- Add to pom.xml -->
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>7.6.0</version>
</dependency>
```

```java
@Component
public class RateLimitingInterceptor implements HandlerInterceptor {
    
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                            Object handler) throws Exception {
        String key = request.getRemoteAddr(); // or use username
        Bucket bucket = cache.computeIfAbsent(key, k -> createBucket());
        
        if (bucket.tryConsume(1)) {
            return true;
        } else {
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("Rate limit exceeded");
            return false;
        }
    }
    
    private Bucket createBucket() {
        // Allow 20 requests per minute
        Bandwidth limit = Bandwidth.classic(20, Refill.intervally(20, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}
```

### Documentation

**Required Documentation:**
- [ ] API documentation (Swagger/OpenAPI)
- [ ] Deployment guide (environment setup, dependencies)
- [ ] Database schema documentation
- [ ] Security best practices for production
- [ ] Runbook for common issues
- [ ] Performance tuning guide

**Swagger Setup:**
```xml
<!-- Add to pom.xml -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

Access Swagger UI at: `http://localhost:8080/swagger-ui.html`

### Load Testing Script (JMeter Example)

**Test Scenarios:**
1. **Login Load Test:** 100 concurrent users login over 60 seconds
2. **Upload Load Test:** 50 users upload documents simultaneously
3. **Evaluation Load Test:** 30 concurrent evaluation requests

**Expected Results:**
- 95th percentile response time <3s
- Error rate <1%
- Server CPU usage <80%

### Checklist
- [ ] Performance: key endpoints respond <3s under nominal load
- [ ] Logging: structured logs for errors/info; sensitive data not logged
- [ ] Validation: DTO validation annotations and global exception handling
- [ ] Security: headers (CORS, CSP where applicable), SQL injection/XSS mitigation
- [ ] Rate limiting or throttling for critical endpoints (auth/upload)
- [ ] Monitoring/health checks exposed
- [ ] Deployment/runbook documented (env vars, ports, secrets management)
- [ ] Basic load test or profiling report
- [ ] Swagger/OpenAPI documentation generated
- [ ] Database indexing optimized for performance

---

## General Notes & Best Practices

### Branch Management
- Each team member is responsible for creating their respective feature branch from the `main` branch.
- Regularly commit and push changes to the remote repository.
- Coordinate with the team leader for code reviews and integration.
- Ensure all code changes are thoroughly tested before merging into `main`.
- Use meaningful commit messages following conventional commits format:
  - `feat(module): add new feature`
  - `fix(module): resolve bug`
  - `docs(module): update documentation`
  - `refactor(module): improve code structure`

### Testing Strategy
- Write unit tests for all service methods
- Integration tests for API endpoints
- End-to-end tests for critical user flows
- Aim for >80% code coverage
- Use JUnit 5 and Mockito for backend testing
- Use React Testing Library for frontend testing

### Code Quality
- Follow Java naming conventions (camelCase for variables, PascalCase for classes)
- Use meaningful variable and method names
- Add JavaDoc comments for public methods
- Keep methods short and focused (Single Responsibility Principle)
- Avoid code duplication (DRY principle)
- Use dependency injection instead of tight coupling

### Collaboration
- Daily standups (10-15 minutes) to sync progress
- Update task status in this document regularly
- Ask for help early if blocked
- Share knowledge and help teammates when possible
- Code reviews should be constructive and timely

### Presentation Preparation (December 12, 2025)
- Prepare demo scenarios for each module
- Test the complete user flow (registration → upload → evaluation → feedback)
- Prepare slides with architecture diagrams and screenshots
- Practice presentation timing (15-20 minutes recommended)
- Prepare for Q&A about implementation choices

---

## Quick Reference: Key Technologies

| Technology | Purpose | Documentation |
|------------|---------|---------------|
| **Spring Boot 3.x** | Backend framework | [docs.spring.io](https://docs.spring.io/spring-boot/docs/current/reference/html/) |
| **React 18** | Frontend library | [react.dev](https://react.dev) |
| **Tailwind CSS** | Styling | [tailwindcss.com/docs](https://tailwindcss.com/docs) |
| **MySQL 8** | Database | [dev.mysql.com/doc](https://dev.mysql.com/doc/) |
| **JWT** | Authentication | [jwt.io](https://jwt.io) |
| **Apache PDFBox** | PDF parsing | [pdfbox.apache.org](https://pdfbox.apache.org) |
| **Apache POI** | DOCX parsing | [poi.apache.org](https://poi.apache.org) |
| **OpenRouter API** | AI integration | [openrouter.ai/docs](https://openrouter.ai/docs) |

---

## Emergency Contacts

| Member | Role | Contact |
|--------|------|---------|
| Lawas, Jose Raphael | Team Leader | [Add contact info] |
| Lapure, Jessie Noel | Module 2 | [Add contact info] |
| Laborada, John Joseph | Module 3 | [Add contact info] |
| Pepito, John Patrick | Module 4 | [Add contact info] |
| Verano, Joel | NFRs | [Add contact info] |

---

**Last Updated:** December 5, 2025  
**Next Sync:** December 6, 2025 (Morning Standup)