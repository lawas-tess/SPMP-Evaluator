# Team Task Assignments

This document outlines the task assignments for each team member based on the designated modules of the SPMP-Evaluator project. Each team member is responsible for implementing their respective module in a dedicated feature branch.

---

## Module 1: Basic Login and Security System
**Branch:** `feature/Lawas`

**Assigned To:** Lawas (Team Leader)

**Tasks:**
- Implement user registration and login functionality.
- Secure password storage using encryption.
- Add session management and JWT-based authentication.
- Implement password reset functionality.
- Ensure role-based access control (Student, Professor, Project Manager).
- Write unit tests for authentication and security features.

**Checklist:**
- [ ] Register/login endpoints validated with happy-path and invalid credentials
- [ ] Password hashing in place (BCrypt) and verified in DB
- [ ] JWT issued with expiry, refresh or re-login flow decided
- [ ] Role-based access control enforced on protected endpoints
- [ ] Password reset (token flow or change-password with old password) working
- [ ] CORS configured for frontend origins
- [ ] Basic rate-limiting or brute-force mitigation for auth
- [ ] Auth unit/integration tests green
- [ ] Swagger or README notes updated for auth routes

---

## Module 2: Role-Based User Interface Transactions
**Branch:** `feature/Lapure`

**Assigned To:** Lapure

**Tasks:**
- Build role-specific dashboards for Students and Professors.
- Implement document upload, edit, and delete functionality for Students.
- Create task management UI for Professors (create, update, delete tasks).
- Add submission tracker for Professors to view student submissions.
- Implement evaluation feedback display for Students.
- Write integration tests for role-based UI components.

**Checklist:**
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

**Assigned To:** Laborada

**Tasks:**
- Develop the document parser to extract text from PDF and DOCX files.
- Implement IEEE 1058 section detection using keyword matching.
- Integrate the parser with the compliance evaluation service.
- Handle file validation and error reporting for unsupported formats.
- Write unit tests for the parser module.

**Checklist:**
- [ ] PDF and DOCX extraction verified with sample files (normal and edge cases)
- [ ] Keyword matching covers all IEEE 1058 sections per constants
- [ ] Parser returns meaningful errors for corrupt/unsupported files
- [ ] Performance: extraction <10s for 50-page doc
- [ ] Unit tests for extraction and keyword detection pass
- [ ] Service integrates parser output into evaluation flow
- [ ] Security: input sanitized; no temp file leaks

---

## Module 4: Generate Score and Feedback
**Branch:** `feature/Pepito`

**Assigned To:** Pepito

**Tasks:**
- Implement the scoring mechanism for compliance evaluation.
- Generate structured feedback based on evaluation results.
- Create APIs to retrieve compliance scores and feedback.
- Ensure scoring weights are configurable (structure vs completeness).
- Write unit tests for the scoring and feedback module.

**Checklist:**
- [ ] Scoring formula implemented with configurable weights (structure/completeness)
- [ ] Section analyses persisted and retrievable via DTOs
- [ ] Feedback text clear for missing/present sections
- [ ] API returns overall score, per-section scores, compliance flag
- [ ] Thresholds (e.g., min compliance) configurable
- [ ] Unit/integration tests for scoring logic and API responses
- [ ] Handles re-evaluation idempotently (updates existing scores)

---

## Non-Functional Requirements
**Branch:** `feature/Verano`

**Assigned To:** Verano

**Tasks:**
- Ensure the application meets performance benchmarks (e.g., response time < 3 seconds).
- Implement logging and monitoring for backend services.
- Add input validation and error handling across all modules.
- Ensure the application is secure (e.g., prevent SQL injection, XSS).
- Write documentation for deployment and maintenance.

**Checklist:**
- [ ] Performance: key endpoints respond <3s under nominal load
- [ ] Logging: structured logs for errors/info; sensitive data not logged
- [ ] Validation: DTO validation annotations and global exception handling
- [ ] Security: headers (CORS, CSP where applicable), SQL injection/XSS mitigation
- [ ] Rate limiting or throttling for critical endpoints (auth/upload)
- [ ] Monitoring/health checks exposed
- [ ] Deployment/runbook documented (env vars, ports, secrets management)
- [ ] Basic load test or profiling report

---

## General Notes
- Each team member is responsible for creating their respective feature branch from the `main` branch.
- Regularly commit and push changes to the remote repository.
- Coordinate with the team leader for code reviews and integration.
- Ensure all code changes are thoroughly tested before merging into `main`.