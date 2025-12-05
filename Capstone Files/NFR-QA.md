# Non-Functional Requirements (NFR) QA Test Cases

This document defines test cases to verify the system's **Performance**, **Security**, **Reliability**, **Accuracy**, and **Consistency** requirements.

> **Legend:** ✅ = Passed | 🔄 = Pending | ❌ = Failed

---

## 1. Performance Test Cases

*Measuring Speed and Efficiency*

### NFR-P-1: File Processing Time (<10s)

| Field | Description |
|:------|:------------|
| **Requirement** | File Processing Time (<10s) |
| **Test Objective** | Verify that a large SPMP document is processed and evaluated within 10 seconds. |
| **Status** | 🔄 Pending |

**Test Steps:**
1. Upload a **50-page document**
2. Start a timer immediately upon upload confirmation
3. Stop the timer when the evaluation score/feedback is displayed

**Expected Result:** The total elapsed time for processing and evaluation is **≤ 10 seconds**.

---

### NFR-P-2: Score Generation Time (<5s)

| Field | Description |
|:------|:------------|
| **Requirement** | Score Generation Time (<5s) |
| **Test Objective** | Verify that the final score is generated quickly after parsing is complete. |
| **Status** | 🔄 Pending |

**Test Steps:**
1. Use a pre-parsed document (or wait for parsing to complete)
2. Trigger the score generation process
3. Measure the time until the score is calculated and displayed

**Expected Result:** The score generation completes in **≤ 5 seconds**.

---

## 2. Security Test Cases

*Verifying Access Control*

### NFR-S-2a: Access Control (Student)

| Field | Description |
|:------|:------------|
| **Requirement** | Access Control (Student) |
| **Test Objective** | Verify a **Student** cannot access or execute Professor-only functions (e.g., Override Score, Configure Parser). |
| **Status** | ✅ Passed |

**Test Steps:**
1. Log in as a **Student**
2. Attempt to navigate directly to the "Override Score" or "Configure Parser" screens
3. If visible, attempt to click the "Override Score" button

**Expected Result:** The system denies access, displays an "Unauthorized" message, or the option is **not visible/disabled** for the Student role.

---

### NFR-S-2b: Access Control (Professor)

| Field | Description |
|:------|:------------|
| **Requirement** | Access Control (Professor) |
| **Test Objective** | Verify a **Professor** can access all Professor-only functions. |
| **Status** | ✅ Passed |

**Test Steps:**
1. Log in as a **Professor**
2. Successfully navigate to "Configure Parser" (UC-3.2) and "Override Score" (UC-4.3)

**Expected Result:** The Professor successfully accesses and loads both the configuration and override features without errors.

---

### NFR-S-4: Score Modification

| Field | Description |
|:------|:------------|
| **Requirement** | Score Modification |
| **Test Objective** | Verify only authorized users (Prof/PM) can view/modify scores. |
| **Status** | ✅ Passed |

**Test Steps:**
1. Log in as a **Student**
2. Navigate to the view feedback page
3. Attempt to find and use any button to modify or override the score

**Expected Result:** The Student is only allowed to **view** the score; modification options are **not present or inactive**.

---

## 3. Reliability Test Cases

*Ensuring Uptime*

### NFR-R-1: Operational Uptime (High Load)

| Field | Description |
|:------|:------------|
| **Requirement** | Operational Uptime (High Load) |
| **Test Objective** | Verify the system remains available under concurrent user load. |
| **Status** | 🔄 Pending |

**Test Steps:**
1. Use a load testing tool to simulate **20 concurrent users** attempting to log in and upload files over a 1-minute period

**Expected Result:** All simulated users are able to complete their tasks successfully with no server errors or crashes.

---

### NFR-R-2: Service Recovery

| Field | Description |
|:------|:------------|
| **Requirement** | Service Recovery |
| **Test Objective** | Verify the system recovers quickly if an AI service fails temporarily. |
| **Status** | 🔄 Pending |

**Test Steps:**
1. Trigger an intentional, temporary interruption of the AI Scoring Service
2. Attempt to upload a new document during the interruption
3. After restoring the service, verify that the document is successfully processed

**Expected Result:** The system handles the interruption gracefully (e.g., queues the request) and processes the document successfully once the service is restored.

---

## 4. Accuracy and Consistency Test Cases

*Validating AI/Parser Reliability*

### NFR-A-1: Parser Accuracy (Flaw Detection)

| Field | Description |
|:------|:------------|
| **Requirement** | Parser Accuracy (Flaw Detection) |
| **Test Objective** | Verify the parser can accurately detect compliance flaws in a document. |
| **Status** | 🔄 Pending |

**Test Steps:**
1. Create a "gold standard" document with **4 deliberate IEEE 1058 compliance flaws** (e.g., missing Table of Contents, incorrect section numbering)
2. Upload and process the document
3. Review the parser feedback (UC-3.3)

**Expected Result:** The parser correctly identifies the 4 deliberate flaws, achieving a high accuracy (85-90% minimum).

---

### NFR-C-1: Parser Determinism

| Field | Description |
|:------|:------------|
| **Requirement** | Parser Determinism |
| **Test Objective** | Verify that the parser produces the same results for identical inputs. |
| **Status** | 🔄 Pending |

**Test Steps:**
1. Upload **Document A** and record its compliance feedback (e.g., missing Clause 4.1)
2. Immediately re-upload the **exact same Document A** (or a clean copy, Document B)
3. Compare the compliance feedback generated for Document A and Document B

**Expected Result:** The parser feedback for both Document A and Document B is **identical**.

---

### NFR-C-2: Scoring Determinism

| Field | Description |
|:------|:------------|
| **Requirement** | Scoring Determinism |
| **Test Objective** | Verify that the same file always results in the same score (unless manually overridden). |
| **Status** | 🔄 Pending |

**Test Steps:**
1. Upload Document A and record the system-generated score (e.g., 88/100)
2. Ensure no override has occurred
3. Re-generate the score for Document A (if possible) or upload an identical copy (Document B)

**Expected Result:** The score generated for the second run/copy **must be exactly the same** as the first score (88/100).

---

## Test Summary

| Category | Test ID | Requirement | Status |
|:---------|:--------|:------------|:------:|
| Performance | NFR-P-1 | File Processing Time (<10s) | 🔄 |
| Performance | NFR-P-2 | Score Generation Time (<5s) | 🔄 |
| Security | NFR-S-2a | Access Control (Student) | ✅ |
| Security | NFR-S-2b | Access Control (Professor) | ✅ |
| Security | NFR-S-4 | Score Modification | ✅ |
| Reliability | NFR-R-1 | Operational Uptime (High Load) | 🔄 |
| Reliability | NFR-R-2 | Service Recovery | 🔄 |
| Accuracy | NFR-A-1 | Parser Accuracy (Flaw Detection) | 🔄 |
| Consistency | NFR-C-1 | Parser Determinism | 🔄 |
| Consistency | NFR-C-2 | Scoring Determinism | 🔄 |

**Passed: 3/10 | Pending: 7/10 | Failed: 0/10**