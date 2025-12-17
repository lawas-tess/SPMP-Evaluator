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

---

# 2.3 Admin: System Administration & User Management

## Overview
This section documents all administrative use cases (UC 2.11 - UC 2.15) for system administrators managing users, assignments, and audit activities.

---

## Activity Diagrams

### Activity Diagram 1: Admin Create User

```
[Start]
↓
Admin opens Dashboard
↓
Admin clicks "Create User"
↓
Admin fills User Form (Name, Email, Role)
↓
System validates user data
↓
Decision:
  - If invalid → Show validation error message
  - If valid → System creates user account → System logs action → User Created
↓
[End]
```

### Activity Diagram 2: Admin Edit User

```
[Start]
↓
Admin opens User Management
↓
Admin selects user from list
↓
Admin modifies user details (Name, Email, Role, Status)
↓
System validates changes
↓
Decision:
  - If invalid → Show validation error
  - If valid → System updates user record → System logs action → User Updated
↓
[End]
```

### Activity Diagram 3: Admin Delete User

```
[Start]
↓
Admin opens User Management
↓
Admin selects user to delete
↓
System prompts confirmation dialog
↓
Decision:
  - If cancel → Return to user list
  - If confirm → System cascade-deletes user data → System logs deletion → User Deleted
↓
[End]
```

### Activity Diagram 4: Admin View Audit Log

```
[Start]
↓
Admin navigates to Audit Log section
↓
System displays paginated log entries
↓
Admin applies filters (Date, User, Action Type)
↓
System updates display with filtered results
↓
Decision:
  - Export logs → System generates CSV/PDF → Download complete
  - View details → System displays detailed log entry → Admin reviews
↓
[End]
```

### Activity Diagram 5: Admin System Settings

```
[Start]
↓
Admin navigates to System Settings
↓
Admin selects settings category
↓
Admin modifies configuration values
↓
System validates new settings
↓
Decision:
  - If invalid → Show validation error
  - If valid → Admin confirms changes → System applies settings → System logs change → Settings Updated
↓
Decision (Requires Restart?):
  - Yes → System shows restart warning
  - No → Settings applied immediately
↓
[End]
```

---

## UC 2.11: Admin View & Manage Users

| Field | Description |
|:------|:------------|
| **Use Case Name** | Admin View & Manage Users |
| **Primary Actor** | Administrator |
| **Secondary Actors** | Database, User Management Module |
| **Description** | Allows administrators to **view a list of all users** in the system, including students, professors, and other admins, with filtering and search capabilities. |
| **Preconditions** | Administrator is logged in and has admin privileges. |
| **Postconditions** | Admin can view, search, and filter the complete user list. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Admin navigates to User Management section | ✅ |
| 2 | System displays all users with role, email, and status | ✅ |
| 3 | Admin can filter by role (Student/Professor/Admin) | ✅ |
| 4 | Admin can search by name or email | ✅ |
| 5 | Admin can sort by date created, last login, or status | ✅ |
| 6 | System displays user details on selection | ✅ |

### Alternative Flows
- **No users found:** System displays "No users matching criteria" message
- **Export list:** Admin can export user list as CSV

### Exceptions
- **Database error:** System displays error message and retry option

---

## UC 2.12: Admin Create User

| Field | Description |
|:------|:------------|
| **Use Case Name** | Admin Create User |
| **Primary Actor** | Administrator |
| **Secondary Actors** | Database, Email Service |
| **Description** | Enables administrators to **create new user accounts** directly in the system with assigned role (Student, Professor, Admin). |
| **Preconditions** | Administrator is logged in with admin privileges. |
| **Postconditions** | New user account is created and stored in the database. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Admin opens Create User form | ✅ |
| 2 | Admin enters name, email, role | ✅ |
| 3 | System validates email format and uniqueness | ✅ |
| 4 | Admin sets temporary password or auto-generates one | ✅ |
| 5 | Admin confirms user creation | ✅ |
| 6 | System creates user account and logs action | ✅ |

### Alternative Flows
- **Email already exists:** System displays error and suggests recovery or alternative email
- **Auto-send credentials:** Admin can send temporary password via email

### Exceptions
- **Invalid input:** System shows validation error and prompts correction
- **Database error:** System prevents creation and displays error message

---

## UC 2.13: Admin Edit User

| Field | Description |
|:------|:------------|
| **Use Case Name** | Admin Edit User |
| **Primary Actor** | Administrator |
| **Secondary Actors** | Database |
| **Description** | Allows administrators to **modify user details** such as name, email, role, and status. |
| **Preconditions** | Administrator is logged in and user to be edited exists. |
| **Postconditions** | User details are updated in the database and changes are logged. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Admin selects a user from the user list | ✅ |
| 2 | Admin opens user edit form | ✅ |
| 3 | Admin modifies user details (name, email, role, status) | ✅ |
| 4 | System validates changes | ✅ |
| 5 | Admin confirms changes | ✅ |
| 6 | System updates user record and logs action | ✅ |

### Alternative Flows
- **Change role:** Admin can change user role (e.g., Student → Professor)
- **Disable account:** Admin can disable/enable user account without deletion

### Exceptions
- **Email conflict:** System prevents duplicate email and prompts alternative
- **Invalid data:** System shows validation error and retains original values

---

## UC 2.14: Admin Delete User

| Field | Description |
|:------|:------------|
| **Use Case Name** | Admin Delete User |
| **Primary Actor** | Administrator |
| **Secondary Actors** | Database |
| **Description** | Enables administrators to **remove user accounts** from the system and cascade-delete associated assignments and tasks. |
| **Preconditions** | Administrator is logged in and user to be deleted exists. |
| **Postconditions** | User account and all associated data (assignments, tasks) are removed from the system. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Admin selects user to delete | ✅ |
| 2 | System prompts for confirmation | ✅ |
| 3 | Admin confirms deletion | ✅ |
| 4 | System cascade-deletes user, assignments, and tasks | ✅ |
| 5 | System logs deletion action | ✅ |
| 6 | System displays success message | ✅ |

### Alternative Flows
- **Soft delete:** System can archive user instead of permanent deletion
- **Audit trail:** Deleted user data is preserved in audit log

### Exceptions
- **User not found:** System displays error and updates list
- **Deletion failure:** System retains user and displays error message

---

## UC 2.15: Admin View Audit Log

| Field | Description |
|:------|:------------|
| **Use Case Name** | Admin View Audit Log |
| **Primary Actor** | Administrator |
| **Secondary Actors** | Database, Audit Log Module |
| **Description** | Allows administrators to **access comprehensive audit logs** of all system activities, including user actions, file uploads, deletions, and system changes. |
| **Preconditions** | Administrator is logged in with admin privileges. |
| **Postconditions** | Admin can view, filter, and export audit logs for compliance and security analysis. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:----:|:------:|
| 1 | Admin navigates to Audit Log section | ✅ |
| 2 | System displays all logged activities with timestamp, user, action | ✅ |
| 3 | Admin can filter by date range | ✅ |
| 4 | Admin can filter by user or action type | ✅ |
| 5 | Admin can view detailed log entry | ✅ |
| 6 | Admin can export logs as CSV or PDF | ✅ |

### Alternative Flows
- **Advanced search:** Admin can search by keyword or action code
- **Real-time monitoring:** Admin can view live system activity stream
- **Alert setup:** Admin can configure alerts for specific activities

### Exceptions
- **No logs found:** System displays "No logs matching criteria" message
- **Export error:** System shows error and suggests retry

---

## Admin Role Summary

| Feature | Status | API Endpoint | Frontend Component |
|---------|--------|--------------|-------------------|
| View Users | ✅ DONE | `GET /api/admin/users` | `UserManagement.jsx` |
| Create User | ✅ DONE | `POST /api/admin/users` | `UserManagement.jsx` |
| Edit User | ✅ DONE | `PUT /api/admin/users/{id}` | `UserManagement.jsx` |
| Delete User | ✅ DONE | `DELETE /api/admin/users/{id}` | `UserManagement.jsx` |
| Assign Students | ✅ DONE | `POST /api/admin/assignments` | `StudentAssignmentForm.jsx` |
| View Audit Log | ✅ DONE | `GET /api/admin/audit-logs` | `AuditLogViewer.jsx` |
| System Reports | ✅ DONE | `GET /api/admin/reports/*` | `AdminReports.jsx` |
| System Settings | ✅ DONE | `GET/POST /api/admin/settings` | `SystemSettingsForm.jsx` |

**All 5 Admin Features (UC 2.11-2.15) Fully Implemented: Backend + Frontend + Database**

---

# 2.3 System Administrator (SRS Format)

## Use Case Diagram

**Actors:**
- System Administrator

**Use Cases:**
- Manage Users (Create/Edit/Delete)
- View User List
- View Audit Log

**Relationships:**
- Administrator → View User List
- Administrator → Manage Users (Create/Edit/Delete)
- Administrator → View Audit Log
- Manage Users → includes View User List

**System Boundary:**
- Admin Dashboard Module

---

## Use Case Description

| Section | Details |
|---------|---------|
| **Use Case ID** | UC 2.3 |
| **Use Case Name** | System Administrator Dashboard & User Management |
| **Primary Actors** | System Administrator |
| **Secondary Actor(s)** | Database, User Management Module, Audit Log Module, Email Service |
| **Description** | The administrator can:<br/>• View all users in the system with filtering and search<br/>• Create, edit, and delete user accounts<br/>• Assign roles to users (Student, Professor, Admin)<br/>• View comprehensive audit logs of all system activities<br/>• Monitor user compliance and system security |
| **Preconditions** | Administrator is logged in with admin privileges. User list is loaded. |
| **Postconditions** | User management actions are saved and logged. Audit trail is maintained. |

---

## Basic Flow

### A. User Management (View/Create/Edit/Delete)

1. Administrator opens the Admin Dashboard
2. System displays all users with:
   - User name & email
   - Role (Student/Professor/Admin)
   - Status (Active/Inactive)
   - Date Created
   - Last Login

3. Administrator can:
   - **Filter by role:** Select Student, Professor, or Admin
   - **Search by name/email:** Enter keyword to find users
   - **Sort by:** Date Created, Last Login, or Status
   - **Create User:** Click "Add User" button
   - **Edit User:** Click user row to edit details
   - **Delete User:** Select user and click "Delete" (with confirmation)

4. **Create User Flow:**
   - Administrator clicks "Create User"
   - System opens User Form with fields:
     - Full Name (required)
     - Email (required, must be unique)
     - Role (Student/Professor/Admin)
     - Temporary Password (auto-generated or custom)
   - Administrator fills form and clicks "Create"
   - System validates email uniqueness
   - System creates user account and logs action
   - System displays success message

5. **Edit User Flow:**
   - Administrator selects user from list
   - System opens Edit Form with current details
   - Administrator modifies name, email, role, or status
   - System validates changes (e.g., email uniqueness)
   - Administrator clicks "Save"
   - System updates user record and logs action
   - System displays success message

6. **Delete User Flow:**
   - Administrator selects user from list
   - System prompts confirmation: "This will delete user and all associated data"
   - Administrator confirms
   - System cascade-deletes user, tasks, and assignments
   - System logs deletion action
   - System displays success message and refreshes user list

### B. Audit Log Viewing

1. Administrator opens Audit Log section
2. System displays all logged activities with:
   - Timestamp
   - User who performed action
   - Action type (Create/Edit/Delete/Login/etc.)
   - Resource affected
   - Details

3. Administrator can:
   - **Filter by date range:** Select start and end date
   - **Filter by user:** Select specific user to view their actions
   - **Filter by action type:** Select Create, Edit, Delete, Login, etc.
   - **Search:** Enter keyword or action code
   - **View details:** Click log entry to see full details
   - **Export logs:** Download as CSV or PDF

4. System displays activities such as:
   - User Login attempts (success/failure)
   - User Account created/edited/deleted
   - Document uploaded/deleted
   - Feedback provided
   - Tasks assigned/completed
   - System settings changed

---

### Alternative Flows

| Scenario | Flow |
|----------|------|
| **Email already exists** | System prevents creation with error: "Email already in use". Admin can recover account or use different email. |
| **Role change request** | Admin can change user role (Student → Professor, etc.). System notifies user of role change. |
| **Account disable** | Admin can disable user account without deletion. Disabled users cannot log in but data is preserved. |
| **Bulk user creation** | Admin can import users via CSV file (name, email, role). System validates and creates accounts. |
| **Export user list** | Admin can export all users or filtered results as CSV. |
| **Real-time activity** | Admin can view live system activity stream for monitoring. |
| **No audit logs found** | System displays "No logs matching criteria" message. |

---

### Exceptions

| Exception | Resolution |
|-----------|-----------|
| **Database error** | System displays error message and provides retry option. |
| **Invalid input** | System shows validation error (e.g., "Email format invalid") and prompts correction. |
| **Duplicate email** | System prevents duplicate email and suggests recovery or alternative. |
| **User not found** | System updates list and displays "User not found" message. |
| **Deletion failure** | System retains user account and displays error message. |
| **Export error** | System shows error and suggests retry or alternative format. |
| **Requires restart** | System warns if settings changes require application restart. |
| **Export error** | System shows error and suggests retry or alternative format. |
| **Requires restart** | System warns if settings changes require application restart. |

---

