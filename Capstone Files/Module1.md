# Module 1: Basic Login and Security System

## Use Case Descriptions (UC 1.1 - 1.2)

This module documents all use cases for the SPMP Evaluator system related to **Authentication** and **Security**.

> **Legend:** ✅ = Implemented | 🔄 = In Progress | ❌ = Not Started

---

## UC 1.1: Register User

| Field | Description |
|:------|:------------|
| **Use Case Name** | Register User |
| **Primary Actor** | User (Student, Professor, Admin) |
| **Secondary Actors** | System |
| **Description** | Allow new users to create an account with **unique credentials and assigned role** (Student, Professor, or Admin). Includes password encryption behavior. |
| **Preconditions** | User has access to registration interface. System is operational. User has valid email address. Database is accessible. |
| **Postconditions** | New user account created. Password encrypted and stored securely. User credentials saved in database. User can now login to system. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | User navigates to registration page | ✅ |
| 2 | User enters email, password, and selects role | ✅ |
| 3 | System validates input (unique email, password strength) | ✅ |
| 4 | System encrypts password using BCrypt | ✅ |
| 5 | System stores user in database | ✅ |
| 6 | System displays success message | ✅ |

### Alternative Flows
- **Email already exists:** System displays error and prompts for different email
- **Weak password:** System displays password requirements

### Exceptions
- **Database error:** System displays error message and suggests retry

---

## UC 1.2: Login

| Field | Description |
|:------|:------------|
| **Use Case Name** | Login |
| **Primary Actor** | User (Student, Professor, Admin) |
| **Secondary Actors** | System |
| **Description** | Authenticate user credentials and **grant system access**. Includes role-based access determination and session management (JWT). |
| **Preconditions** | User has registered account. System is operational. Database is accessible. User has valid credentials. |
| **Postconditions** | User authenticated. JWT token generated and stored. Role-based permissions applied. User session established. User redirected to role-appropriate dashboard. |

### Basic Flow ✅ ALL IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | User navigates to login page | ✅ |
| 2 | User enters email and password | ✅ |
| 3 | System validates credentials against database | ✅ |
| 4 | System generates JWT token with role claims | ✅ |
| 5 | System redirects to role-appropriate dashboard | ✅ |

### Alternative Flows
- **Invalid credentials:** System displays error message
- **Account locked:** System displays lockout message with retry time

### Exceptions
- **Authentication service unavailable:** System displays error and suggests retry

---

## Implementation Summary

| Use Case | Description | Status |
|:---------|:------------|:------:|
| UC 1.1 | Register User | ✅ Complete |
| UC 1.2 | Login | ✅ Complete |

**Total: 2/2 Use Cases Implemented (100%)**