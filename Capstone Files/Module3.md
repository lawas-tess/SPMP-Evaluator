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

### Current Architecture

**Current Implementation (Hybrid Approach):**

1. **Document Parsing - Keyword-Based** ✅
   - `DocumentParser.java` - Extracts text from PDF/DOCX using Apache PDFBox + Apache POI
   - `IEEE1058StandardConstants.java` - Defines IEEE 1058 section keywords
   - Pattern matching and keyword detection for clause identification

2. **Compliance Scoring - Keyword-Based** ✅
   - `ComplianceEvaluationService.java` - Deterministic scoring using weighted keywords
   - Calculates section coverage percentages (0-100%) based on keyword matches
   - Fast, deterministic, and consistent scoring

3. **AI Feedback Generation - OpenRouter (Optional)** ✅
   - `OpenRouterService.java` - AI model: `amazon/nova-lite-v1:free`
   - Generates human-readable recommendations and missing clause analysis
   - Currently used for supplementary feedback only, not main scoring
   - Falls back to mock data if API key not configured

### Architecture Diagram

```
Student Upload (PDF/DOCX)
         ↓
[DocumentParser.java]
  Text Extraction
         ↓
[IEEE1058StandardConstants.java]
  Pattern Matching
         ↓
[ComplianceEvaluationService.java]
  Keyword-Based Scoring (0-100%)
         ↓
  Main Score ✅
  (Deterministic)
         ↓
[OpenRouterService.java] (Optional)
  AI Recommendations
         ↓
[ParserFeedbackService.java]
  Combine Score + Recommendations
         ↓
Feedback Display (Score + AI insights)
```

### Why Keyword-Based for Scoring?

- ✅ **Deterministic** - Same document always gets same score
- ✅ **Fast** - No API calls, instant feedback
- ✅ **Consistent** - IEEE 1058 weighted scoring
- ✅ **Reliable** - No API quota issues
- ✅ **Transparent** - Clear why sections scored low

### Why OpenRouter for Recommendations?

- ✅ **Natural language** - "Consider adding risk mitigation strategies..."
- ✅ **Context-aware** - Understands document content
- ✅ **Flexible** - AI can suggest contextual improvements
- ✅ **Optional** - Can fall back to template recommendations

---

## Implementation Summary

| Use Case | Description | Implementation | Status |
|:---------|:------------|:---------------:|:------:|
| UC 3.1 | Upload SPMP Document | PDF/DOCX extraction | ✅ |
| UC 3.2 | Configure Parser Module | Keyword weights config | ✅ |
| UC 3.3 | View Parser Feedback | Keyword scoring + AI recommendations | ✅ |

> **Current State:**
> - ✅ Backend entities: `ParserConfiguration`, `ParserFeedback`, `SectionAnalysis`
> - ✅ Repository interfaces: CRUD operations for configurations and feedback
> - ✅ Service layer: Configuration management, keyword-based evaluation, optional AI feedback
> - ✅ REST API: `/api/parser/*` endpoints for config and feedback
> - ✅ Frontend UI: Parser Configuration component for professors
> - ✅ Frontend UI: Parser Feedback viewer for compliance scores
> - ✅ Default IEEE 1058 configuration with all standard clauses
>
> **Parsing & Scoring:**
> - ✅ `DocumentParser.java` - PDF/DOCX text extraction (Apache PDFBox + Apache POI)
> - ✅ `IEEE1058StandardConstants.java` - Section keywords and structure definitions
> - ✅ `ComplianceEvaluationService.java` - Keyword-based weighted scoring logic
>
> **AI Integration (Optional):**
> - ✅ `OpenRouterService.java` - AI-based recommendations via OpenRouter API
> - ✅ `ParserFeedbackService.java` - Integrates keyword score with AI recommendations
>
> **Configuration:**
> - Scoring Provider: Keyword-based (deterministic)
> - Recommendations Provider: OpenRouter (`amazon/nova-lite-v1:free`)
> - Fallback: Mock recommendations if API unavailable

---

# System Design Document (SDD) - Document Parsing & Compliance

## 3.1 Document Upload & Parsing (UC 3.1)

### Front-end Component(s)

**Component Name:** `DocumentUpload.jsx` (Enhanced for parsing)

**Description and purpose:**
Drag-and-drop interface for uploading SPMP documents. Displays file validation status and parsing queue information.

**Component type or format:**
React Component with file type validation for PDF/DOCX and progress tracking.

---

### Back-end Component(s)

**Component Name:** `DocumentController.java` - POST /api/documents/parse

**Description and purpose:**
REST endpoint that accepts document upload and triggers parsing workflow.

**Component type or format:**
Spring Boot REST Controller with multipart file handling.

---

**Component Name:** `DocumentParser.java`

**Description and purpose:**
Handles PDF/DOCX text extraction using Apache PDFBox and Apache POI libraries.

**Component type or format:**
Utility class with document parsing methods for multiple file formats.

---

**Component Name:** `ComplianceEvaluationService.java`

**Description and purpose:**
Performs keyword-based IEEE 1058 compliance scoring against extracted text.

**Component type or format:**
Spring Service class with scoring algorithm implementation.

---

### Object-Oriented Components

**Class Diagram:**
```plantuml
@startuml DocumentParsing_ClassDiagram
!theme plain
left to right direction
skinparam backgroundColor #FEFEFE
skinparam classBackgroundColor #F0F0F0

class DocumentUpload {
  - file: File
  - parsing: boolean
  --
  + handleUpload(): void
  + trackProgress(): void
}

class DocumentController {
  - parserService: ParserService
  --
  + uploadAndParse(): ResponseEntity
}

class DocumentParser {
  - extractText(): String
  - parseFromPDF(): String
  - parseFromDOCX(): String
}

class ComplianceEvaluationService {
  - ieee1058Config: Configuration
  --
  + evaluateDocument(): Double
  + scoreSections(): Map
  + detectMissingClauses(): List
}

class ParserFeedback {
  - score: Double
  - detectedClauses: List
  - missingClauses: List
}

DocumentUpload --> DocumentController
DocumentController --> DocumentParser
DocumentParser --> ComplianceEvaluationService
ComplianceEvaluationService --> ParserFeedback
@enduml
```

**Sequence Diagram:**
```plantuml
@startuml DocumentParsing_Sequence
!theme plain
participant "Student" as Student
participant "DocumentUpload" as UI
participant "DocumentController" as Ctrl
participant "DocumentParser" as Parser
participant "ComplianceEvaluationService" as Eval
participant "Database" as DB

Student -> UI: Upload SPMP
activate UI
UI -> Ctrl: POST /api/documents/parse
activate Ctrl
Ctrl -> Parser: parseDocument()
activate Parser
Parser -> Parser: Extract text (PDF/DOCX)
Parser --> Ctrl: Extracted text
deactivate Parser
Ctrl -> Eval: evaluateCompliance()
activate Eval
Eval -> Eval: Score sections
Eval -> Eval: Detect gaps
Eval --> Ctrl: Compliance score
deactivate Eval
Ctrl -> DB: save(ParserFeedback)
activate DB
deactivate DB
Ctrl --> UI: Response
deactivate Ctrl
UI --> Student: Show results
deactivate UI
@enduml
```

---

## 3.2 Configure Parser Module (UC 3.2)

### Front-end Component(s)

**Component Name:** `ParserConfiguration.jsx`

**Description and purpose:**
Form interface for professors to configure parser settings, keyword weights, and scoring parameters.

**Component type or format:**
React Component with configuration form and settings management.

---

### Back-end Component(s)

**Component Name:** `ParserConfigurationController.java`

**Description and purpose:**
REST endpoints for parser configuration CRUD operations.

**Component type or format:**
Spring Boot REST Controller with configuration management endpoints.

---

**Component Name:** `IEEE1058StandardConstants.java`

**Description and purpose:**
Defines IEEE 1058 standard sections, keywords, and default weights.

**Component type or format:**
Java utility class with constants and configuration maps.

---

### Object-Oriented Components

**Class Diagram:**
```plantuml
@startuml ParserConfiguration_ClassDiagram
!theme plain
left to right direction
skinparam backgroundColor #FEFEFE
skinparam classBackgroundColor #F0F0F0

class ParserConfiguration {
  - sections: Section[]
  - weights: Map<String, Double>
  --
  + updateWeights(): void
  + saveConfiguration(): void
}

class ParserConfigurationController {
  - configService: ConfigurationService
  --
  + getConfiguration(): ResponseEntity
  + updateConfiguration(): ResponseEntity
}

class ConfigurationService {
  - configRepository: ConfigurationRepository
  - ieee1058: IEEE1058StandardConstants
  --
  + loadConfiguration(): Configuration
  + saveConfiguration(): Configuration
  + validateWeights(): boolean
}

class Configuration {
  - id: Long
  - professor: User
  - sections: Map<String, SectionConfig>
  - weights: Map<String, Double>
}

ParserConfiguration --> ParserConfigurationController
ParserConfigurationController --> ConfigurationService
ConfigurationService --> Configuration
@enduml
```

**Sequence Diagram:**
```plantuml
@startuml ParserConfiguration_Sequence
!theme plain
participant "Professor" as Prof
participant "ParserConfig" as UI
participant "ConfigController" as Ctrl
participant "ConfigService" as Service
participant "Database" as DB

Prof -> UI: Open Configuration
activate UI
UI -> Ctrl: GET /api/parser/config
activate Ctrl
Ctrl -> Service: loadConfiguration()
activate Service
Service -> DB: Query configuration
activate DB
DB --> Service: Configuration
deactivate DB
Service --> Ctrl: Config
deactivate Service
Ctrl --> UI: Response
deactivate Ctrl
UI --> Prof: Display form
deactivate UI

Prof -> UI: Modify weights
activate UI
UI -> Ctrl: PUT /api/parser/config
activate Ctrl
Ctrl -> Service: updateConfiguration()
activate Service
Service -> Service: Validate weights
Service -> DB: Update record
activate DB
deactivate DB
Service --> Ctrl: Success
deactivate Service
Ctrl --> UI: Confirm
deactivate Ctrl
UI --> Prof: Show success
deactivate UI
@enduml
```

---

## 3.3 View Parser Feedback (UC 3.3)

### Front-end Component(s)

**Component Name:** `ParserFeedback.jsx`

**Description and purpose:**
Displays parser feedback including compliance score, section analysis, detected clauses, gaps, and recommendations.

**Component type or format:**
React Component with data visualization and section breakdowns.

---

### Back-end Component(s)

**Component Name:** `ParserFeedbackController.java`

**Description and purpose:**
REST endpoints for retrieving parser feedback and analysis results.

**Component type or format:**
Spring Boot REST Controller with feedback retrieval endpoints.

---

**Component Name:** `OpenRouterService.java` (Optional)

**Description and purpose:**
Optional AI service for generating contextual recommendations using OpenRouter API.

**Component type or format:**
Spring Service class with OpenRouter integration for AI-generated suggestions.

---

### Object-Oriented Components

**Class Diagram:**
```plantuml
@startuml ParserFeedback_ClassDiagram
!theme plain
left to right direction
skinparam backgroundColor #FEFEFE
skinparam classBackgroundColor #F0F0F0

class ParserFeedback {
  - score: Double
  - detectedClauses: List
  - missingClauses: List
  --
  + renderScore(): void
  + displayAnalysis(): void
}

class ParserFeedbackController {
  - feedbackService: FeedbackService
  --
  + getFeedback(): ResponseEntity
  + getAnalysis(): ResponseEntity
}

class FeedbackService {
  - feedbackRepository: FeedbackRepository
  - openRouterService: OpenRouterService
  --
  + retrieveFeedback(): ParserFeedback
  + generateRecommendations(): String
  + aggregateAnalysis(): Map
}

class OpenRouterService {
  - apiClient: OpenRouterClient
  --
  + generateRecommendations(): String
  + analyzeGaps(): List
}

ParserFeedback --> ParserFeedbackController
ParserFeedbackController --> FeedbackService
FeedbackService --> OpenRouterService
@enduml
```

**Sequence Diagram:**
```plantuml
@startuml ParserFeedback_Sequence
!theme plain
participant "Student" as Student
participant "ParserFeedback" as UI
participant "FeedbackController" as Ctrl
participant "FeedbackService" as Service
participant "OpenRouter" as AI
participant "Database" as DB

Student -> UI: View Feedback
activate UI
UI -> Ctrl: GET /api/parser/feedback/{docId}
activate Ctrl
Ctrl -> Service: retrieveFeedback()
activate Service
Service -> DB: Query feedback
activate DB
DB --> Service: ParserFeedback
deactivate DB
Service -> AI: generateRecommendations()
activate AI
AI --> Service: Recommendations
deactivate AI
Service --> Ctrl: Complete feedback
deactivate Service
Ctrl --> UI: Response
deactivate Ctrl
UI -> UI: Render score & analysis
UI --> Student: Display feedback
deactivate UI
@enduml
```

---

**Data Design:**

```sql
CREATE TABLE parser_feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL,
    compliance_score DOUBLE,
    detected_clauses LONGTEXT,
    missing_clauses LONGTEXT,
    section_analysis JSON,
    recommendations LONGTEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (document_id) REFERENCES spmp_documents(id) ON DELETE CASCADE,
    INDEX idx_document_id (document_id)
);

CREATE TABLE parser_configurations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    professor_id BIGINT NOT NULL,
    section_weights JSON,
    keyword_config JSON,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (professor_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_professor_id (professor_id)
);
```

---