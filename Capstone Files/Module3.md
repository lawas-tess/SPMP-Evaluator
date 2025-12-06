# Module 3: Automated Document Parsing and Compliance

## Use Case Descriptions (UC 3.1 - 3.3)

This module documents all use cases for the SPMP Evaluator system related to **Document Parsing** and **IEEE 1058 Compliance**.

> **Legend:** ✅ = Implemented | 🔄 = In Progress | ❌ = Not Started

---

## UC 3.1: Upload SPMP Document

| Field | Description |
|:------|:------------|
| **Use Case Name** | Upload SPMP Document |
| **Primary Actor** | Student |
| **Secondary Actors** | Parser Module |
| **Description** | Allows a **Student** to upload an SPMP document for automated compliance evaluation against the **IEEE 1058 standard**. The system validates the file, preprocesses it, and forwards it to the parser module for clause detection and AI-based compliance analysis. |
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

### Basic Flow 🔄 PARTIALLY IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Professor navigates to parser configuration | ✅ |
| 2 | Professor views current IEEE 1058 clause mappings | ✅ |
| 3 | Professor adjusts clause weights | ✅ |
| 4 | Professor defines custom rule mappings | ✅ |
| 5 | System validates configuration | ✅ |
| 6 | System saves and applies configuration | ✅ |

> **Note:** Parser Configuration UI and backend are implemented. Configurations can be created, edited, and saved. Default IEEE 1058 configuration available. AI parser integration pending to use these configurations for actual document analysis.

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
| **Primary Actor** | Student, Professor |
| **Secondary Actors** | System |
| **Description** | Allows authorized users to view the **structured parser feedback** generated from the evaluation of uploaded SPMP documents. Feedback includes compliance scores, missing IEEE 1058 clauses, and AI-generated recommendations for improvement. |
| **Preconditions** | SPMP document must have been successfully uploaded and processed. Parser feedback must be generated and stored. User must be authenticated with appropriate role. |
| **Postconditions** | User successfully views structured compliance feedback. System logs access activity for auditing and version tracking. |

### Basic Flow 🔄 PARTIALLY IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | User navigates to feedback section | ✅ |
| 2 | User selects evaluated document | ✅ |
| 3 | System retrieves parser feedback | ✅ (Mock data) |
| 4 | System displays compliance scores | ✅ (Mock data) |
| 5 | System shows missing clauses and recommendations | ✅ (Mock data) |
| 6 | System logs viewing activity | ✅ |

> **Note:** UI and data structure are complete with mock feedback generation. Database entities and API endpoints ready. Requires AI parser integration to generate real feedback instead of mock data.

### Alternative Flows
- **Export feedback:** User downloads feedback as PDF/CSV
- **Compare versions:** User views feedback differences between document versions

### Exceptions
- **Parsing incomplete:** System shows "Processing" status with estimated time

---

## Implementation Summary

| Use Case | Description | Status |
|:---------|:------------|:------:|
| UC 3.1 | Upload SPMP Document | 🔄 Upload complete, parser integration pending |
| UC 3.2 | Configure Parser Module | 🔄 UI and backend ready, AI integration pending |
| UC 3.3 | View Parser Feedback | 🔄 UI ready with mock data, AI parser pending |

**Total: 3/3 Use Cases Foundationally Implemented (Mock Data Ready)**

> **Current State:**
> - ✅ Backend entities: `ParserConfiguration`, `ParserFeedback`
> - ✅ Repository interfaces: CRUD operations for configurations and feedback
> - ✅ Service layer: Configuration management, mock feedback generation
> - ✅ REST API: `/api/parser/*` endpoints for config and feedback
> - ✅ Frontend UI: Parser Configuration component for professors
> - ✅ Frontend UI: Parser Feedback viewer for compliance scores
> - ✅ Default IEEE 1058 configuration with all standard clauses
>
> **Next Step - AI Parser Integration:**
> The infrastructure is ready. To complete Module 3, implement:
> 1. Document parsing engine (IEEE 1058 clause detection using NLP/AI)
> 2. AI-based compliance analysis (GPT-4, Claude, or custom model)
> 3. Replace mock feedback with real AI-generated analysis
>
> **What Works:** All CRUD operations, UI components, data flow, and mock demonstrations.