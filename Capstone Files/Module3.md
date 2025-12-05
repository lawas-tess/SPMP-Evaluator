# Module 3: Automated Document Parsing and Compliance

## Use Case Descriptions (UC 3.1 - 3.3)

This module documents all use cases for the SPMP Evaluator system related to **Document Parsing** and **IEEE 1058 Compliance**.

> **Legend:** ✅ = Implemented | 🔄 = In Progress | ❌ = Not Started

---

## UC 3.1: Upload SPMP Document

| Field | Description |
|:------|:------------|
| **Use Case Name** | Upload SPMP Document |
| **Primary Actor** | Student, Project Manager |
| **Secondary Actors** | Parser Module |
| **Description** | Allows a **Student or Project Manager** to upload an SPMP document for automated compliance evaluation against the **IEEE 1058 standard**. The system validates the file, preprocesses it, and forwards it to the parser module for clause detection and AI-based compliance analysis. |
| **Preconditions** | User must be authenticated. Parser module must be configured by the Professor. SPMP document must be in a supported format. |
| **Postconditions** | Document is stored and queued for parsing. Parser module receives document for analysis. |

### Basic Flow 🔄 PARTIALLY IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | User navigates to upload section | ✅ |
| 2 | User selects SPMP document file | ✅ |
| 3 | System validates file format and size | ✅ |
| 4 | System stores document in database | ✅ |
| 5 | System forwards document to parser module | ❌ Parser not implemented |
| 6 | System displays upload confirmation | ✅ |

> **Note:** File upload UI and storage are complete. Parser module integration is pending.

### Alternative Flows
- **Invalid format:** System displays error and lists supported formats
- **File too large:** System rejects and suggests compression

### Exceptions
- **Parser unavailable:** System queues document for later processing

---

## UC 3.2: Configure Parser Module

| Field | Description |
|:------|:------------|
| **Use Case Name** | Configure Parser Module |
| **Primary Actor** | Professor |
| **Secondary Actors** | System |
| **Description** | Allows the **Professor to configure the Automated Parser Module** by adjusting clause weights, defining rule mappings, and setting evaluation parameters to align with the IEEE 1058 standard. |
| **Preconditions** | Professor must be authenticated. Parser module must be initialized. System access permissions must allow configuration changes. |
| **Postconditions** | Parser configuration (rules, clause weights, and criteria) is saved successfully. Updated configuration is applied to future SPMP evaluations. |

### Basic Flow ❌ NOT IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Professor navigates to parser configuration | ✅ (UI placeholder) |
| 2 | Professor views current IEEE 1058 clause mappings | ❌ |
| 3 | Professor adjusts clause weights | ✅ (GradingCriteria covers this) |
| 4 | Professor defines custom rule mappings | ❌ |
| 5 | System validates configuration | ❌ |
| 6 | System saves and applies configuration | ❌ |

> **Note:** Clause weight adjustment is available via GradingCriteria (UC 2.7). Full parser configuration requires parser module.

### Alternative Flows
- **Reset to defaults:** Professor restores original IEEE 1058 mappings
- **Import configuration:** Professor loads pre-defined configuration template

### Exceptions
- **Invalid weights:** System prevents saving and highlights errors

---

## UC 3.3: View Parser Feedback

| Field | Description |
|:------|:------------|
| **Use Case Name** | View Parser Feedback |
| **Primary Actor** | Student, Project Manager, Professor |
| **Secondary Actors** | System |
| **Description** | Allows authorized users to view the **structured parser feedback** generated from the evaluation of uploaded SPMP documents. Feedback includes compliance scores, missing IEEE 1058 clauses, and AI-generated recommendations for improvement. |
| **Preconditions** | SPMP document must have been successfully uploaded and processed. Parser feedback must be generated and stored. User must be authenticated with appropriate role. |
| **Postconditions** | User successfully views structured compliance feedback. System logs access activity for auditing and version tracking. |

### Basic Flow ❌ NOT IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | User navigates to feedback section | ✅ |
| 2 | User selects evaluated document | ✅ |
| 3 | System retrieves parser feedback | ❌ Parser not implemented |
| 4 | System displays compliance scores | ❌ No AI scores generated |
| 5 | System shows missing clauses and recommendations | ❌ |
| 6 | System logs viewing activity | ✅ |

> **Note:** UI navigation and activity logging are complete. Requires parser module to generate feedback.

### Alternative Flows
- **Export feedback:** User downloads feedback as PDF/CSV
- **Compare versions:** User views feedback differences between document versions

### Exceptions
- **Parsing incomplete:** System shows "Processing" status with estimated time

---

## Implementation Summary

| Use Case | Description | Status |
|:---------|:------------|:------:|
| UC 3.1 | Upload SPMP Document | 🔄 Upload works, parser pending |
| UC 3.2 | Configure Parser Module | ❌ Not Implemented |
| UC 3.3 | View Parser Feedback | ❌ Not Implemented |

**Total: 0/3 Use Cases Fully Implemented**

> **Blocker:** The AI Parser Module is not yet implemented. This module requires:
> 1. Document parsing engine (IEEE 1058 clause detection)
> 2. AI-based compliance analysis
> 3. Feedback generation with recommendations
>
> **What Works:** File upload, storage, and basic UI navigation are complete.