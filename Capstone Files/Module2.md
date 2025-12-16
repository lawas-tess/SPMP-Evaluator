# Module 2: Role-Based User Interface Transactions

## Use Case Descriptions (UC 2.1 - 2.10)

This module documents all use cases for the SPMP Evaluator system related to **Student** and **Professor** roles.

> **Legend:** ✅ = Implemented | 🔄 = In Progress | ❌ = Not Started

---

## UC 2.1: Student File Upload

| Field | Description |
|:------|:------------|
| **Use Case Name** | Student File Upload |
| **Primary Actor** | Student |
| **Secondary Actors** | None |
| **Description** | Enables students to upload project-related files (**SPMP documents**) to the system for evaluation and feedback. |
| **Preconditions** | Student is logged in and has a valid SPMP document ready for upload. |
| **Postconditions** | File is stored in the system and available for evaluation. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Student navigates to the upload section | ✅ |
| 2 | Student selects the file to upload | ✅ |
| 3 | System validates file format and size | ✅ |
| 4 | Student confirms upload | ✅ |
| 5 | System processes and stores the file | ✅ |
| 6 | System displays upload confirmation | ✅ |

### Alternative Flows
- **Invalid file format:** System displays error message and prompts for correct format
- **File too large:** System rejects upload and suggests compression

---

## UC 2.2: Student File Edit

| Field | Description |
|:------|:------------|
| **Use Case Name** | Student File Edit |
| **Primary Actor** | Student |
| **Secondary Actors** | None |
| **Description** | Allows students to **replace or update previously uploaded project files** before evaluation deadlines. |
| **Preconditions** | Student is logged in and has previously uploaded a file that can still be edited. |
| **Postconditions** | Updated file replaces the original and is queued for re-evaluation. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Student views list of uploaded files | ✅ |
| 2 | Student selects a file to edit | ✅ |
| 3 | Student uploads replacement file | ✅ |
| 4 | System validates new file | ✅ |
| 5 | Student confirms changes | ✅ |
| 6 | System updates file and logs change | ✅ |

### Alternative Flows
- **File past deadline:** System prevents edit and shows deadline notice

### Exceptions
- **Validation failure:** System retains original file and prompts for corrections

---

## UC 2.3: Student File Removal

| Field | Description |
|:------|:------------|
| **Use Case Name** | Student File Removal |
| **Primary Actor** | Student |
| **Secondary Actors** | None |
| **Description** | Enables students to **delete their uploaded files** if they are before the submission deadline. |
| **Preconditions** | Student is logged in and has uploaded files that are still within the removal window. |
| **Postconditions** | File is permanently deleted from the system. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Student views list of uploaded files | ✅ |
| 2 | Student selects a file for removal | ✅ |
| 3 | System prompts for confirmation | ✅ |
| 4 | Student confirms deletion | ✅ |
| 5 | System removes file and logs action | ✅ |

### Alternative Flows
- **File past deadline:** System disables removal option

### Exceptions
- **Deletion failure:** System retains file and shows error message

---

## UC 2.4: Student View Feedback

| Field | Description |
|:------|:------------|
| **Use Case Name** | Student View Feedback |
| **Primary Actor** | Student |
| **Secondary Actors** | Professor (indirect, provides feedback) |
| **Description** | Students can **access detailed feedback, comments, and grades** provided by professors on their uploaded files. |
| **Preconditions** | Student is logged in and has uploaded files that have been evaluated. |
| **Postconditions** | Students can access detailed feedback, comments, and grades provided by professors. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Student navigates to feedback section | ✅ |
| 2 | Student selects a specific file | ✅ |
| 3 | System displays evaluation results | ✅ |
| 4 | Student reviews comments and scores | ✅ |
| 5 | System tracks view activity | ✅ |

### Alternative Flows
- **No evaluation yet:** System shows "Pending evaluation" status

### Exceptions
- **System error:** Display error message and suggest retry

---

## UC 2.5: Student Task Tracking

| Field | Description |
|:------|:------------|
| **Use Case Name** | Student Task Tracking |
| **Primary Actor** | Student |
| **Secondary Actor** | Professor (assigns tasks) |
| **Description** | Students can **view tasks assigned to them**, including deadlines, progress, and completion status. |
| **Preconditions** | Student is logged in. Tasks have been assigned by a Professor. |
| **Postconditions** | Student is aware of current task status and deadlines. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Student accesses task tracking section | ✅ |
| 2 | System displays list of assigned tasks | ✅ |
| 3 | Student views task details and deadlines | ✅ |
| 4 | Student checks progress indicators | ✅ |
| 5 | System updates view activity | ✅ |

### Alternative Flows
- **No tasks assigned:** System shows empty state with helpful message

### Exceptions
- **System unavailable:** Show cached tasks or offline message

---

## UC 2.6: Professor Task Creation

| Field | Description |
|:------|:------------|
| **Use Case Name** | Professor Task Creation |
| **Primary Actor** | Professor |
| **Secondary Actor** | None |
| **Description** | Professors can **create new project tasks for students**, including descriptions, deadlines, and requirements. |
| **Preconditions** | Professor is logged in and has permission to create tasks. |
| **Postconditions** | Task is created and students are notified. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Professor navigates to task creation | ✅ |
| 2 | Professor enters task details | ✅ |
| 3 | Professor sets deadline and requirements | ✅ |
| 4 | Professor assigns to specific students/groups | ✅ |
| 5 | System saves task and notifies students | ✅ |

### Alternative Flows
- **Save as draft:** Task is saved but not published yet

### Exceptions
- **Validation error:** System highlights issues and prevents saving

---

## UC 2.7: Professor Supplement Grading Criteria

| Field | Description |
|:------|:------------|
| **Use Case Name** | Professor Supplement Grading Criteria |
| **Primary Actor** | Professor |
| **Secondary Actor** | None |
| **Description** | Professors can **supplement AI-generated evaluations with custom grading rubrics and criteria**. |
| **Preconditions** | Professor is logged in and has access to grading tools. |
| **Postconditions** | Custom grading criteria are saved and applied to future student evaluations. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Professor selects evaluation criteria section | ✅ |
| 2 | Professor defines custom criteria | ✅ |
| 3 | Professor sets weightings for each criterion | ✅ |
| 4 | Professor saves criteria for use in evaluations | ✅ |
| 5 | System applies to future evaluations | ✅ |

### Alternative Flows
- **Load template:** Use pre-defined criteria templates

### Exceptions
- **System validation failure:** Criteria are reset, and error message is displayed

---

## UC 2.8: Professor Override AI Results

| Field | Description |
|:------|:------------|
| **Use Case Name** | Professor Override AI Results |
| **Primary Actor** | Professor |
| **Secondary Actors** | None |
| **Description** | Professors can review AI-generated evaluations and **manually override scores or feedback** if necessary. |
| **Preconditions** | AI evaluation has been completed for a student's submission. |
| **Postconditions** | Overridden results are final and logged for auditing. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Professor views AI evaluation results | ✅ |
| 2 | Professor reviews scores and feedback | ✅ |
| 3 | Professor makes manual adjustments | ✅ |
| 4 | Professor adds justification for override | ✅ |
| 5 | System saves override and notifies student | ✅ |

### Alternative Flows
- **Accept AI results:** No changes needed, just confirm

### Exceptions
- **Permission denied:** Ensure professor has rights for specific student

---

## UC 2.9: Professor Update Tasks

| Field | Description |
|:------|:------------|
| **Use Case Name** | Professor Update Tasks |
| **Primary Actor** | Professor |
| **Secondary Actor** | Students (affected by changes) |
| **Description** | Professors can **modify existing task instructions, deadlines, or requirements** as needed. |
| **Preconditions** | Task exists and professor has edit permissions. |
| **Postconditions** | Task is updated and students are informed of changes. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Professor selects task to edit | ✅ |
| 2 | Professor modifies instructions/deadlines | ✅ |
| 3 | System validates changes | ✅ |
| 4 | Professor confirms updates | ✅ |
| 5 | System saves and notifies affected students | ✅ |

### Alternative Flows
- **Extend deadline:** Automatically notify students of extension

### Exceptions
- **Invalid deadline:** Prevent setting past dates or invalid times

---

## UC 2.10: Professor Monitor Student Progress

| Field | Description |
|:------|:------------|
| **Use Case Name** | Professor Monitor Student Progress |
| **Primary Actor** | Professor |
| **Secondary Actors** | None |
| **Description** | Professors can **track student submission status, review evaluation scores, and monitor overall class performance trends**. |
| **Preconditions** | Professor is logged in and students have submitted files for evaluation. |
| **Postconditions** | Professor has a comprehensive overview of class progress. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Professor navigates to the progress dashboard | ✅ |
| 2 | System displays a class-wide summary | ✅ |
| 3 | Professor filters by student or group | ✅ |
| 4 | Professor reviews individual submission status and scores | ✅ |
| 5 | System logs viewing activity | ✅ |

### Alternative Flows
- **Export data:** Professor downloads summary data in CSV format

### Exceptions
- **Data retrieval failure:** System displays offline or error message

---

## Implementation Summary

| Use Case | Description | Status |
|:---------|:------------|:------:|
| UC 2.1 | Student File Upload | ✅ Complete |
| UC 2.2 | Student File Edit | ✅ Complete |
| UC 2.3 | Student File Removal | ✅ Complete |
| UC 2.4 | Student View Feedback | ✅ Complete |
| UC 2.5 | Student Task Tracking | ✅ Complete |
| UC 2.6 | Professor Task Creation | ✅ Complete |
| UC 2.7 | Professor Supplement Grading Criteria | ✅ Complete |
| UC 2.8 | Professor Override AI Results | ✅ Complete |
| UC 2.9 | Professor Update Tasks | ✅ Complete |
| UC 2.10 | Professor Monitor Student Progress | ✅ Complete |
| UC 2.11 | Admin User Management | ✅ Complete |
| UC 2.12 | Admin Assign Students to Professors | ✅ Complete |
| UC 2.13 | Admin View Audit Logs | ✅ Complete |
| UC 2.14 | Admin System Reports | ✅ Complete |
| UC 2.15 | Admin System Settings | ✅ Complete |

**Total: 15/15 Use Cases Implemented (100%)**

---

## UC 2.11: Admin User Management

| Field | Description |
|:------|:------------|
| **Use Case Name** | Admin User Management |
| **Primary Actor** | Admin |
| **Secondary Actors** | None |
| **Description** | Admins can **create, view, update, and delete user accounts** (professors and students), including resetting passwords and managing user roles. |
| **Preconditions** | Admin is logged in and has admin privileges. |
| **Postconditions** | User accounts are created, updated, or deleted, and all changes are logged for auditing. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Admin navigates to user management section | ✅ |
| 2 | Admin selects action (create, view, edit, delete) | ✅ |
| 3 | System displays user list or creation form | ✅ |
| 4 | Admin enters or modifies user information | ✅ |
| 5 | System validates input data | ✅ |
| 6 | Admin confirms changes | ✅ |
| 7 | System saves changes and logs action | ✅ |
| 8 | System sends notification to affected user | ✅ |

### Alternative Flows
- **Reset password:** Admin can generate and send password reset link to user
- **Lock/unlock account:** Admin can temporarily disable or enable user accounts
- **Bulk import:** Admin can upload CSV file to create multiple users

### Exceptions
- **Duplicate email:** System prevents creation of users with existing email
- **Invalid role:** System rejects invalid role assignments

---

## UC 2.12: Admin Assign Students to Professors

| Field | Description |
|:------|:------------|
| **Use Case Name** | Admin Assign Students to Professors |
| **Primary Actor** | Admin |
| **Secondary Actors** | Professors, Students (notified) |
| **Description** | Admins can **assign students to professors** for supervision and evaluation, managing the student-professor relationships in the system. |
| **Preconditions** | Admin is logged in, and both students and professors exist in the system. |
| **Postconditions** | Students are assigned to professors, and both parties are notified of the assignment. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Admin navigates to student assignment section | ✅ |
| 2 | Admin selects professor from list | ✅ |
| 3 | System displays available students | ✅ |
| 4 | Admin selects one or multiple students | ✅ |
| 5 | Admin confirms assignment | ✅ |
| 6 | System creates assignment relationships | ✅ |
| 7 | System sends notifications to professor and students | ✅ |
| 8 | System logs assignment action | ✅ |

### Alternative Flows
- **Reassign student:** Admin can change student's assigned professor
- **Bulk assignment:** Admin can assign multiple students to one professor at once
- **Remove assignment:** Admin can unassign students from professors

### Exceptions
- **Student already assigned:** System warns before reassigning
- **Professor at capacity:** System warns if professor has too many students

---

## UC 2.13: Admin View Audit Logs

| Field | Description |
|:------|:------------|
| **Use Case Name** | Admin View Audit Logs |
| **Primary Actor** | Admin |
| **Secondary Actors** | None |
| **Description** | Admins can **view comprehensive audit logs** of all system activities, including user actions, document uploads, evaluations, and administrative changes. |
| **Preconditions** | Admin is logged in and has audit log access permissions. |
| **Postconditions** | Admin has visibility into system activity and can identify security issues or anomalies. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Admin navigates to audit log section | ✅ |
| 2 | System displays paginated log entries | ✅ |
| 3 | Admin applies filters (user, date, action type) | ✅ |
| 4 | System updates display with filtered results | ✅ |
| 5 | Admin reviews log details | ✅ |
| 6 | Admin exports logs if needed | ✅ |

### Alternative Flows
- **Search by user:** Admin can filter logs by specific user
- **Search by date range:** Admin can view logs within specific time period
- **Export to CSV:** Admin can download logs for external analysis

### Exceptions
- **No logs found:** System displays message when no logs match criteria
- **Export size too large:** System prompts admin to narrow date range

---

## UC 2.14: Admin System Reports

| Field | Description |
|:------|:------------|
| **Use Case Name** | Admin System Reports |
| **Primary Actor** | Admin |
| **Secondary Actors** | None |
| **Description** | Admins can **generate comprehensive system reports** including user statistics, document submission trends, evaluation metrics, and system usage analytics. |
| **Preconditions** | Admin is logged in and system has sufficient data for reporting. |
| **Postconditions** | Reports are generated and can be viewed or exported for stakeholder review. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Admin navigates to reports section | ✅ |
| 2 | Admin selects report type | ✅ |
| 3 | Admin sets parameters (date range, filters) | ✅ |
| 4 | System generates report | ✅ |
| 5 | System displays report with visualizations | ✅ |
| 6 | Admin reviews report data | ✅ |
| 7 | Admin exports report (PDF/Excel) if needed | ✅ |

### Alternative Flows
- **Scheduled reports:** Admin can schedule automatic report generation
- **Custom reports:** Admin can create custom report templates
- **Dashboard view:** Admin can pin important metrics to dashboard

### Exceptions
- **Insufficient data:** System warns when data is too limited for meaningful report
- **Report generation timeout:** System provides option to run report asynchronously

---

## UC 2.15: Admin System Settings

| Field | Description |
|:------|:------------|
| **Use Case Name** | Admin System Settings |
| **Primary Actor** | Admin |
| **Secondary Actors** | None |
| **Description** | Admins can **configure system-wide settings** including registration controls, evaluation parameters, notification settings, and system maintenance modes. |
| **Preconditions** | Admin is logged in and has system configuration permissions. |
| **Postconditions** | System settings are updated and take effect immediately or as scheduled. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Admin navigates to system settings | ✅ |
| 2 | Admin selects settings category | ✅ |
| 3 | System displays current configuration | ✅ |
| 4 | Admin modifies settings | ✅ |
| 5 | System validates new settings | ✅ |
| 6 | Admin confirms changes | ✅ |
| 7 | System applies settings and logs change | ✅ |
| 8 | System notifies users if settings affect them | ✅ |

### Alternative Flows
- **Maintenance mode:** Admin can enable system maintenance mode
- **Registration controls:** Admin can open/close student/professor registration
- **Feature toggles:** Admin can enable/disable specific system features

### Exceptions
- **Invalid configuration:** System prevents saving invalid settings
- **Requires restart:** System warns when changes require application restart

