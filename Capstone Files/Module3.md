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

### Basic Flow ✅ IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | User navigates to upload section | ✅ |
| 2 | User selects SPMP document file | ✅ |
| 3 | System validates file format and size | ✅ |
| 4 | System stores document in database | ✅ |
| 5 | System forwards document to parser module | ✅ `DocumentParser.java` |
| 6 | System displays upload confirmation | ✅ |

> **Note:** Complete implementation with PDF/DOCX text extraction using Apache PDFBox and Apache POI.

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

### Basic Flow ✅ IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | Professor navigates to parser configuration | ✅ |
| 2 | Professor views current IEEE 1058 clause mappings | ✅ |
| 3 | Professor adjusts clause weights | ✅ |
| 4 | Professor defines custom rule mappings | ✅ |
| 5 | System validates configuration | ✅ |
| 6 | System saves and applies configuration | ✅ |

> **Note:** Parser Configuration fully implemented with backend API, database persistence, and frontend UI.

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

### Basic Flow ✅ IMPLEMENTED

| Step | Action | Status |
|:----:|:-------|:------:|
| 1 | User navigates to feedback section | ✅ |
| 2 | User selects evaluated document | ✅ |
| 3 | System retrieves parser feedback | ✅ `OpenRouterService.java` |
| 4 | System displays compliance scores | ✅ AI-generated |
| 5 | System shows missing clauses and recommendations | ✅ AI-generated |
| 6 | System logs viewing activity | ✅ |

> **Note:** Full AI integration using OpenRouter API with `amazon/nova-lite-v1:free` model for IEEE 1058 compliance analysis.

### Alternative Flows
- **Export feedback:** User downloads feedback as PDF/CSV
- **Compare versions:** User views feedback differences between document versions

### Exceptions
- **Parsing incomplete:** System shows "Processing" status with estimated time

---

## Implementation Summary

| Use Case | Description | Status |
|:---------|:------------|:------:|
| UC 3.1 | Upload SPMP Document | ✅ Complete with PDF/DOCX parsing |
| UC 3.2 | Configure Parser Module | ✅ Full CRUD with UI |
| UC 3.3 | View Parser Feedback | ✅ AI-powered analysis |

**Total: 3/3 Use Cases FULLY IMPLEMENTED ✅**
<<<<<<< HEAD
=======

### Remaining Backlog (for Laborada)
- [ ] **Upgrade to full AI-driven scoring** — Send each section's extracted text to OpenRouter AI and receive:
  - Completeness assessment ("Is this section complete?")
  - Quality score (0-100 based on IEEE 1058 compliance, clarity, depth)
  - Specific missing elements ("Lacks mitigation strategies for identified risks")
  - Contextual recommendations ("Consider adding response plans for each risk")
- [ ] Replace keyword-based coverage scoring in `ComplianceEvaluationService.java` with AI-generated scores
- [ ] Add caching/rate-limiting for AI calls to avoid quota issues
>>>>>>> 13db54f5773fd0118a4994ed9a7a71d10fe2b3b6

> **Current State:**
> - ✅ Backend entities: `ParserConfiguration`, `ParserFeedback`
> - ✅ Repository interfaces: CRUD operations for configurations and feedback
> - ✅ Service layer: Configuration management, AI-powered feedback generation
> - ✅ REST API: `/api/parser/*` endpoints for config and feedback
> - ✅ Frontend UI: Parser Configuration component for professors
> - ✅ Frontend UI: Parser Feedback viewer for compliance scores
> - ✅ Default IEEE 1058 configuration with all standard clauses
>
> **AI Integration Complete:**
> - ✅ `DocumentParser.java` - PDF/DOCX text extraction (Apache PDFBox + Apache POI)
> - ✅ `IEEE1058StandardConstants.java` - Section keywords and structure definitions
> - ✅ `OpenRouterService.java` - AI-based IEEE 1058 compliance analysis
> - ✅ `ParserFeedbackService.java` - Integrated with OpenRouter AI
> - ✅ `ComplianceEvaluationService.java` - Keyword-based scoring logic
>
> **Configuration:**
> - API Provider: OpenRouter
> - Model: `amazon/nova-lite-v1:free`
> - Fallback: Mock data generation if AI unavailable