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

### Use Cases (1.1 - 1.8)

#### Authentication (1.1 - 1.4)

| UC# | Use Case | Backend | Frontend | Priority | Status |
|-----|----------|---------|----------|----------|--------|
| 1.1 | User Registration | ✅ `AuthController.java` | ✅ `AuthPage.jsx` | HIGH | ✅ DONE |
| 1.2 | User Login | ✅ `AuthController.java` | ✅ `AuthPage.jsx` | HIGH | ✅ DONE |
| 1.3 | Password Reset | ⬜ TODO | ⬜ TODO | HIGH | 🔴 TODO |
| 1.4 | Change Password | ⬜ TODO | ⬜ TODO | MEDIUM | 🔴 TODO |

#### Security (1.5 - 1.8)

| UC# | Use Case | Backend | Frontend | Priority | Status |
|-----|----------|---------|----------|----------|--------|
| 1.5 | JWT Token Management | ✅ `JwtService.java` | ✅ `AuthContext.jsx` | HIGH | ✅ DONE |
| 1.6 | Role-Based Access | ✅ `SecurityConfig.java` | ✅ `ProtectedRoute.jsx` | HIGH | ✅ DONE |
| 1.7 | Login Attempt Logging | ⬜ TODO | N/A | MEDIUM | 🔴 TODO |
| 1.8 | Account Lockout | ⬜ TODO | ⬜ TODO | LOW | 🔴 TODO |

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
**Status:** ✅ FULLY IMPLEMENTED 

### Use Cases (2.1 - 2.10)

#### Student Role (2.1 - 2.5)

| UC# | Use Case | Backend | Frontend | Priority | Status |
|-----|----------|---------|----------|----------|--------|
| 2.1 | File Upload | ✅ `DocumentController.java` | ✅ `DocumentUpload.jsx` | HIGH | ✅ DONE |
| 2.2 | File Edit/Replace | ✅ `SPMPDocumentService.java` | ✅ `FileReplaceModal.jsx` | HIGH | ✅ DONE |
| 2.3 | File Removal | ✅ `DocumentController.java` | ✅ `DocumentList.jsx` | MEDIUM | ✅ DONE |
| 2.4 | View Feedback | ✅ `DocumentController.java` + AuditLog | ✅ `EvaluationResults.jsx` | HIGH | ✅ DONE |
| 2.5 | Task Tracking | ✅ `TaskController.java` + AuditLog | ✅ `TaskTracker.jsx` | MEDIUM | ✅ DONE |

#### Professor Role (2.6 - 2.10)

| UC# | Use Case | Backend | Frontend | Priority | Status |
|-----|----------|---------|----------|----------|--------|
| 2.6 | Task Creation | ✅ `TaskService.java` + Notifications | ✅ `TaskManager.jsx` (with student selector) | HIGH | ✅ DONE |
| 2.7 | Grading Criteria | ✅ `GradingCriteriaController.java` | ✅ `GradingCriteria.jsx` | HIGH | ✅ DONE |
| 2.8 | Override AI Results | ✅ `SPMPDocumentService.java` + Notifications | ✅ `ScoreOverride.jsx` | HIGH | ✅ DONE |
| 2.9 | Update Tasks | ✅ `TaskService.java` + Notifications | ✅ `TaskManager.jsx` | MEDIUM | ✅ DONE |
| 2.10 | Monitor Student Progress | ✅ `ReportingController.java` + AuditLog | ✅ `StudentList.jsx` | MEDIUM | ✅ DONE |

### NEW Backend Implementations 

#### New Entities Created
- [x] `GradingCriteria.java` - IEEE 1058 section weights for custom grading
- [x] `Notification.java` - Student notification system for UC 2.6, 2.8, 2.9

#### New Repositories Created
- [x] `GradingCriteriaRepository.java` - findByCreatedBy, findActive methods
- [x] `NotificationRepository.java` - findByUserId, findUnread, countUnread methods

#### New Services Created
- [x] `GradingCriteriaService.java` - CRUD + validation + activate criteria
- [x] `NotificationService.java` - notifyTaskAssigned, notifyTaskUpdated, notifyScoreOverride
- [x] `AuditLogService.java` - logActivity, logView, logViewFeedback, logViewProgress

#### New Controllers Created
- [x] `GradingCriteriaController.java` - Full REST API for grading criteria (UC 2.7)
- [x] `NotificationController.java` - GET/PUT endpoints for notifications (UC 2.8, 2.9)
- [x] `UserController.java` - GET /api/users/students endpoint (UC 2.6, 2.10)

#### Modified Services
- [x] `TaskService.java` - Added NotificationService integration for task creation/update
- [x] `SPMPDocumentService.java` - Added NotificationService for score override

#### Modified Controllers (View Activity Logging)
- [x] `DocumentController.java` - Added AuditLogService for view feedback tracking
- [x] `TaskController.java` - Added AuditLogService for view task tracking
- [x] `ReportingController.java` - Added AuditLogService for progress monitoring

### NEW Frontend Implementations 

#### New Components Created
- [x] `FileReplaceModal.jsx` - Drag-drop file replacement with version notes (UC 2.2)
- [x] `GradingCriteria.jsx` - IEEE 1058 section weight sliders with validation (UC 2.7)
- [x] `StudentList.jsx` - Student table with progress, search/filter (UC 2.10)
- [x] `NotificationBell.jsx` - Notification bell with badge and dropdown (UC 2.8, 2.9)

#### Modified Components
- [x] `Dashboard.jsx` - Added GradingCriteria tab, StudentList tab, FileReplaceModal
- [x] `TaskManager.jsx` - Added student selector dropdown for task assignment (UC 2.6)
- [x] `Navbar.jsx` - Added NotificationBell component

#### API Service Updates
- [x] `apiService.js` - Added userAPI, gradingCriteriaAPI, notificationAPI exports

### Backend API Endpoints Summary

| Endpoint | Method | Description | UC |
|----------|--------|-------------|-----|
| `/api/users/students` | GET | Get all students | 2.6, 2.10 |
| `/api/grading-criteria` | POST/GET | Create/list grading criteria | 2.7 |
| `/api/grading-criteria/{id}` | GET/PUT/DELETE | CRUD operations | 2.7 |
| `/api/grading-criteria/{id}/activate` | PUT | Activate criteria | 2.7 |
| `/api/notifications` | GET | Get all user notifications | 2.8, 2.9 |
| `/api/notifications/unread` | GET | Get unread notifications | 2.8, 2.9 |
| `/api/notifications/count` | GET | Get unread count | 2.8, 2.9 |
| `/api/notifications/{id}/read` | PUT | Mark notification as read | 2.8, 2.9 |
| `/api/notifications/read-all` | PUT | Mark all as read | 2.8, 2.9 |

### Checklist - ALL COMPLETE
- [x] API service covers documents (upload/list/evaluate/replace/delete), tasks (CRUD), reports (stats/progress)
- [x] Student dashboard: upload (drag-drop), list with status, evaluate action, delete, view feedback
- [x] Professor dashboard: submission tracker table with filters, task create/update/delete, score override modal
- [x] File replace endpoint wired on backend and used in UI (FileReplaceModal.jsx)
- [x] Progress/analytics cards for student progress (StudentList.jsx per SRS Module 2.10)
- [x] Role-guarded routes/components; unauthorized access blocked in UI and via API
- [x] Error/loading/toast states for all async flows
- [x] Responsive layout and accessibility checks for key flows
- [x] **NEW: Grading criteria management for professors (UC 2.7)**
- [x] **NEW: Student selector in TaskManager for professor task assignment (UC 2.6)**
- [x] **NEW: Notification system for score override and task updates (UC 2.8, 2.9)**
- [x] **NEW: Activity logging for view feedback, task tracking, progress monitoring (UC 2.4, 2.5, 2.10 Step 5)**
- [x] **NEW: NotificationBell in Navbar for student notification alerts**

#### Dashboard Integration - COMPLETED
- [x] Update `Dashboard.jsx` to render role-specific components
- [x] Add loading states, error handling, toast notifications
- [x] Implement responsive design for mobile/tablet
- [x] Added tab navigation for Documents, Tasks, Grading Criteria, Student List

### Implementation Notes

**Notification System Flow:**
1. Professor creates/updates task → TaskService sends notification to assigned student
2. Professor overrides AI score → SPMPDocumentService sends notification to student
3. Student sees notification bell with unread count → clicks to view and mark as read

**Activity Logging Flow:**
1. Student views feedback → DocumentController logs VIEW_FEEDBACK action
2. Student views tasks → TaskController logs VIEW action  
3. Professor views student progress → ReportingController logs VIEW_PROGRESS action

**Grading Criteria (UC 2.7):**
- IEEE 1058 section weights: Introduction, References, Definitions, Organization, Management, Technical Process, Supporting Process, Additional Plans
- Total weight must equal 100%
- Professors can save, activate, and reuse criteria templates

### Checklist - ALL COMPLETE
- [x] API service covers documents (upload/list/evaluate/replace/delete), tasks (CRUD), reports (stats/progress)
- [x] Student dashboard: upload (drag-drop), list with status, evaluate action, delete, view feedback
- [x] Professor dashboard: submission tracker table with filters, task create/update/delete, score override modal
- [x] File replace endpoint wired on backend and used in UI (FileReplaceModal.jsx)
- [x] Progress/analytics cards for student progress (StudentList.jsx per SRS Module 2.10)
- [x] Role-guarded routes/components; unauthorized access blocked in UI and via API
- [x] Error/loading/toast states for all async flows
- [x] Responsive layout and accessibility checks for key flows
- [x] **NEW: Grading criteria management for professors (UC 2.7)**
- [x] **NEW: Student selector in TaskManager for professor task assignment (UC 2.6)**
- [x] **NEW: Notification system for score override and task updates (UC 2.8, 2.9)**
- [x] **NEW: Activity logging for view feedback, task tracking, progress monitoring (UC 2.4, 2.5, 2.10 Step 5)**
- [x] **NEW: NotificationBell in Navbar for student notification alerts**

---

## Module 3: Automated Parser Module
**Branch:** `feature/Laborada` + `main`  
**Assigned To:** Laborada, John Joseph + AI Integration by Encarnacion  
**Status:** ✅ IMPLEMENTED

### Use Cases (3.1 - 3.6)

#### Document Parsing (3.1 - 3.3)

| UC# | Use Case | Backend | Frontend | Priority | Status |
|-----|----------|---------|----------|----------|--------|
| 3.1 | PDF Text Extraction | ✅ `DocumentParser.java` | N/A | HIGH | ✅ DONE |
| 3.2 | DOCX Text Extraction | ✅ `DocumentParser.java` | N/A | HIGH | ✅ DONE |
| 3.3 | Error Handling for Corrupt Files | ✅ Try-catch with fallback | ⬜ Toast message | HIGH | ✅ DONE |

#### Section Detection (3.4 - 3.6)

| UC# | Use Case | Backend | Frontend | Priority | Status |
|-----|----------|---------|----------|----------|--------|
| 3.4 | Keyword-Based Section Detection | ✅ `IEEE1058StandardConstants.java` | N/A | HIGH | ✅ DONE |
| 3.5 | AI-Powered Section Detection | ✅ `OpenRouterService.java` | N/A | HIGH | ✅ DONE |
| 3.6 | Metadata Extraction (author, date) | ⬜ Optional | ⬜ Display in UI | LOW | 🟡 Optional |

### Current Implementation ✅
- ✅ File upload UI works (from Module 2 UC 2.1)
- ✅ Files are stored in database
- ✅ PDF/DOCX text extraction using Apache PDFBox and Apache POI
- ✅ IEEE 1058 section detection with keyword matching
- ✅ AI-powered compliance analysis via OpenRouter API
- ✅ Parser Configuration UI for professors
- ✅ Parser Feedback viewer with AI-generated scores

### Implementation Details

| Component | Description | Status |
|-----------|-------------|--------|
| `DocumentParser.java` | PDF/DOCX text extraction service | ✅ |
| `IEEE1058StandardConstants.java` | Section keywords and mappings | ✅ |
| `OpenRouterService.java` | AI-based compliance analysis | ✅ |
| `ComplianceEvaluationService.java` | Keyword-based scoring logic | ✅ |
| `ParserFeedbackService.java` | AI-integrated feedback generation | ✅ |

### Dependencies (Already in pom.xml)
```xml
<!-- Apache PDFBox for PDF parsing -->
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>2.0.29</version>
</dependency>

<!-- Apache POI for DOCX parsing -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.3</version>
</dependency>
```

### Remaining Tasks

| Task | Priority | File(s) | Description |
|------|----------|---------|-------------|
| Metadata Extraction | LOW | `DocumentParser.java` | Extract author, creation date, word count |

### Checklist
- [x] PDF and DOCX extraction implemented with sample files
- [x] Keyword matching covers all IEEE 1058 sections per constants
- [x] Parser returns meaningful errors for corrupt/unsupported files
- [x] Performance: extraction <10s for 50-page doc
- [x] Service integrates parser output into evaluation flow
- [x] Security: input sanitized; no temp file leaks
- [x] AI-powered section detection implemented (OpenRouter integration)
- [ ] Unit tests for extraction and keyword detection pass

---

## Module 4: Generate Score & Feedback
**Branch:** `feature/Pepito`  
**Assigned To:** Pepito, John Patrick  
**Status:** ✅ Core Scoring Implemented (backlog reserved for teammate)

### Use Cases (4.1 - 4.8)

#### Scoring System (4.1 - 4.4)

| UC# | Use Case | Backend | Frontend | Priority | Status |
|-----|----------|---------|----------|----------|--------|
| 4.1 | Calculate Overall Compliance Score | ✅ `ComplianceEvaluationService.java` | ✅ `EvaluationResults.jsx` | HIGH | ✅ DONE |
| 4.2 | Apply Custom Rubric (Grading Criteria) | ✅ `GradingCriteriaService.java` | ✅ `GradingCriteria.jsx` | HIGH | ✅ DONE |
| 4.3 | Override AI Results | ✅ `SPMPDocumentService.java` | ✅ `ScoreOverride.jsx` | HIGH | ✅ DONE |
| 4.4 | Score History Tracking | ⬜ `ComplianceScoreHistory.java` | ⬜ History view | LOW | 🔴 TODO (teammate backlog) |

#### Feedback Generation (4.5 - 4.8)

| UC# | Use Case | Backend | Frontend | Priority | Status |
|-----|----------|---------|----------|----------|--------|
| 4.5 | View Overall Score | ✅ `ComplianceEvaluationService.java` | ✅ `EvaluationResults.jsx` | HIGH | ✅ DONE |
| 4.6 | Enhanced Detailed Feedback | ✅ AI feedback via OpenRouter | ✅ Section-by-section display | HIGH | ✅ DONE |
| 4.7 | Re-evaluation of Documents | ⬜ `SPMPDocumentService.java` | ⬜ Re-evaluate button | MEDIUM | 🔴 TODO (teammate backlog) |
| 4.8 | Export Reports (PDF/Excel) | ⬜ `ReportExportService.java` | ⬜ Export button | LOW | 🔴 TODO (teammate backlog) |

### Current Implementation
- ✅ **UC 4.1 Complete:** Overall compliance score calculated using structure + completeness weights
- ✅ **UC 4.2 Complete:** GradingCriteria entity, service, controller, and UI fully working
  - Professors can create custom rubrics with IEEE 1058 section weights
  - Weights must sum to 100%
  - Can save and activate different criteria presets
- ✅ **UC 4.3 Complete:** Score override modal works with AI-generated scores
- ✅ **UC 4.5 Complete:** EvaluationResults.jsx displays overall score and compliance status
- ✅ **UC 4.6 Complete:** Section-by-section analysis with findings and recommendations

### What's Actually Working

| Component | Status | Description |
|-----------|--------|-------------|
| `GradingCriteria.java` | ✅ Complete | Entity with IEEE 1058 section weights |
| `GradingCriteriaDTO.java` | ✅ Complete | Data transfer object |
| `GradingCriteriaRepository.java` | ✅ Complete | CRUD + findByCreatedBy, findActive |
| `GradingCriteriaService.java` | ✅ Complete | Weight validation, CRUD operations |
| `GradingCriteriaController.java` | ✅ Complete | Full REST API |
| `GradingCriteria.jsx` | ✅ Complete | UI with sliders and validation |
| `ScoreOverride.jsx` | ✅ Complete | Modal for overriding AI scores |
| `EvaluationResults.jsx` | ✅ Complete | Displays overall score + section analysis |
| `ComplianceEvaluationService.java` | ✅ Complete | Scoring logic with AI integration |
| `ComplianceScoreRepository.java` | ✅ Complete | Data access with JOIN FETCH queries |

### Remaining Tasks (reserved for teammate contribution)

| Task | Priority | File(s) | Description |
|------|----------|---------|-------------|
| Re-evaluation Logic | MEDIUM | `SPMPDocumentService.java` | Handle re-evaluation of already evaluated documents |
| Score History Tracking | LOW | Create `ComplianceScoreHistory.java` | Track score changes over time |
| Export Reports | LOW | Create `ReportExportService.java` | Export compliance reports as PDF/Excel |
| Audit Log Column Fix | LOW | Database migration | Increase `action` column size in audit_logs table |

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
- [x] Grading criteria UI and backend complete (UC 4.2)
- [x] Score override UI component created (UC 4.3)
- [x] Evaluation results display component ready (UC 4.5)
- [x] Scoring formula implemented with configurable weights
- [x] Section analyses persisted and retrievable via DTOs
- [x] Feedback text clear and actionable for missing/present sections
- [x] API returns overall score, per-section scores, compliance flag
- [ ] Re-evaluation button for already evaluated documents (backlog)
- [ ] Score history tracking across evaluations (backlog)
- [ ] Export reports as PDF/Excel (backlog)
- [ ] Thresholds (e.g., min compliance) configurable via properties
- [ ] Unit/integration tests for scoring logic and API responses
- [ ] Handles re-evaluation idempotently (updates existing scores)

---

## Non-Functional Requirements
**Branch:** `feature/Verano`  
**Assigned To:** Verano, Joel  
**Status:** 🔴 Not Started

### Use Cases (NFR 5.1 - 5.10)

#### Performance (5.1 - 5.3)

| UC# | Use Case | Backend | Frontend | Priority | Status |
|-----|----------|---------|----------|----------|--------|
| 5.1 | API Response <3s | ⬜ Performance profiling | N/A | HIGH | 🔴 TODO |
| 5.2 | Load Testing (JMeter/Gatling) | ⬜ Test scripts | N/A | MEDIUM | 🔴 TODO |
| 5.3 | Database Query Optimization | ⬜ Indexing, caching | N/A | MEDIUM | 🔴 TODO |

#### Security (5.4 - 5.6)

| UC# | Use Case | Backend | Frontend | Priority | Status |
|-----|----------|---------|----------|----------|--------|
| 5.4 | Input Validation & Sanitization | ⬜ DTO validation annotations | ⬜ Form validation | HIGH | 🔴 TODO |
| 5.5 | Rate Limiting (Bucket4j) | ⬜ `RateLimitingInterceptor.java` | N/A | HIGH | 🔴 TODO |
| 5.6 | Security Headers (CSP, XSS) | ⬜ `SecurityConfig.java` | N/A | HIGH | 🔴 TODO |

#### Monitoring & Docs (5.7 - 5.10)

| UC# | Use Case | Backend | Frontend | Priority | Status |
|-----|----------|---------|----------|----------|--------|
| 5.7 | Structured Logging (SLF4J) | ⬜ All service classes | N/A | HIGH | 🔴 TODO |
| 5.8 | Health Checks (Actuator) | ⬜ `application.properties` | N/A | MEDIUM | 🔴 TODO |
| 5.9 | Swagger/OpenAPI Docs | ⬜ `springdoc-openapi` dependency | N/A | MEDIUM | 🔴 TODO |
| 5.10 | Global Exception Handler | ⬜ `GlobalExceptionHandler.java` | ⬜ Error toast | HIGH | 🔴 TODO |

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