# Module 4: AI-Assisted Scoring and Reporting

## Use Case Descriptions (UC 4.1 - 4.5)

This module documents all use cases for the SPMP Evaluator system related to **AI Scoring**, **Custom Rubrics**, and **Feedback Reporting**.

> **Legend:** ✅ = Implemented | 🔄 = In Progress | ❌ = Not Started

---

## UC 4.1: Generate Score & Feedback

| Field | Description |
|:------|:------------|
| **Use Case Name** | Generate Score & Feedback |
| **Primary Actor** | System |
| **Secondary Actors** | Professor, Parser Module |
| **Description** | The system generates compliance scores based on data received from the **Automated Parser Module**. It applies predefined weighted criteria (**Format 20%, Content Relevance 40%, Timeliness 40%**) and attaches parser feedback for transparency. |
| **Preconditions** | Professor is authenticated. Parser data is successfully received. Scoring weights are configured in the system. |
| **Postconditions** | Scores and feedback are stored securely. Reports are accessible to authorized users only. |

### Basic Flow ✅ IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | System receives parsed document data | ✅ |
| 2 | System retrieves configured scoring weights | ✅ |
| 3 | System calculates compliance scores | ✅ |
| 4 | System generates detailed feedback | ✅ |
| 5 | System stores scores and feedback | ✅ |
| 6 | System notifies user of completion | ✅ |

### Alternative Flows
- **Parser error:** System flags document for manual review
- **Weight not configured:** System applies default IEEE 1058 weights

### Exceptions
- **AI service unavailable:** System queues for retry and notifies admin

---

## UC 4.2: Apply Custom Rubric

| Field | Description |
|:------|:------------|
| **Use Case Name** | Apply Custom Rubric |
| **Primary Actor** | Professor |
| **Secondary Actors** | System |
| **Description** | The **Professor defines or modifies a grading rubric** by assigning custom weights to evaluation criteria (e.g., Format, Content, Timeliness). The system validates that the total equals 100% and applies the custom rubric to future score computations. |
| **Preconditions** | Professor is authenticated and authorized. Parser data exists in the system. Default rubric (Format 20%, Content 40%, Timeliness 40%) is already configured. |
| **Postconditions** | Custom rubric is saved in database and applied to all future evaluations. Audit trail records the rubric change (date, time, and user). |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Professor navigates to rubric configuration | ✅ |
| 2 | Professor views current weight distribution | ✅ |
| 3 | Professor adjusts weights using sliders | ✅ |
| 4 | System validates weights sum to 100% | ✅ |
| 5 | Professor saves custom rubric | ✅ |
| 6 | System applies rubric to future evaluations | ✅ |

### Alternative Flows
- **Load template:** Professor selects pre-defined rubric template
- **Reset to default:** Professor restores original weights

### Exceptions
- **Invalid weights:** System prevents saving and highlights error

---

## UC 4.3: Override Score

| Field | Description |
|:------|:------------|
| **Use Case Name** | Override Score |
| **Primary Actor** | Professor |
| **Secondary Actors** | System |
| **Description** | Allows **professors to manually override an SPMP evaluation score**. The action must be justified, and the system logs all overrides with timestamp and justification, updating the official record. |
| **Preconditions** | Professor is authenticated and authorized. A score already exists for the selected SPMP submission. The override justification form is available. |
| **Postconditions** | Original score is preserved for audit. Override score becomes official. Student is notified of score change. |

### Basic Flow ✅ IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Professor views existing score | ✅ |
| 2 | Professor clicks override option | ✅ |
| 3 | Professor enters new score | ✅ |
| 4 | Professor provides justification | ✅ |
| 5 | System validates and saves override | ✅ |
| 6 | System notifies student | ✅ |

### Alternative Flows
- **Cancel override:** Professor discards changes
- **View override history:** Professor sees all previous overrides

### Exceptions
- **Missing justification:** System prevents saving without reason

---

## UC 4.5: View Scores & Feedback

| Field | Description |
|:------|:------------|
| **Use Case Name** | View Scores & Feedback |
| **Primary Actor** | Student, Professor |
| **Secondary Actors** | System |
| **Description** | Allows authenticated users to **view scoring results and feedback**. **Students** view only their own scores; **Professors** view all their students' evaluations. The system ensures proper access control based on user roles. |
| **Preconditions** | User is authenticated and authorized. Scoring and feedback data are available in the system. Role-based permissions are configured. |
| **Postconditions** | User views appropriate scores based on role. System logs viewing activity. |

### Basic Flow ✅ IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | User navigates to scores/feedback section | ✅ |
| 2 | System verifies user role and permissions | ✅ |
| 3 | System retrieves appropriate scores | ✅ |
| 4 | System displays scores and feedback | ✅ |
| 5 | System logs viewing activity | ✅ |

### Alternative Flows
- **Export scores:** User downloads scores as PDF/CSV
- **Filter by date:** User filters scores by submission date

### Exceptions
- **No scores available:** System displays "No evaluations yet" message

---

## Implementation Summary

| Use Case | Description | Status |
|:---------|:------------|:------:|
| UC 4.1 | Generate Score & Feedback | ✅ Complete |
| UC 4.2 | Apply Custom Rubric | ✅ Complete |
| UC 4.3 | Override Score | ✅ Complete |
| UC 4.5 | View Scores & Feedback | ✅ Complete |

**Total: 4/4 Use Cases Fully Implemented (100%)**

### Remaining Backlog (for Pepito)
- [ ] Re-evaluation button for already evaluated documents
- [ ] Score history tracking across evaluations
- [ ] Export reports as PDF/Excel
- [ ] **Update UI to display AI-generated contextual feedback** — When Module 3 upgrades scoring to full AI, adjust `EvaluationResults.jsx` to render richer AI responses (if response shape changes)