package com.team02.spmpevaluator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team02.spmpevaluator.dto.ComplianceReportDTO;
import com.team02.spmpevaluator.dto.ComplianceScoreHistoryDTO;
import com.team02.spmpevaluator.entity.ComplianceScore;
import com.team02.spmpevaluator.entity.ComplianceScoreHistory;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.SPMPDocument;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.ComplianceScoreRepository;
import com.team02.spmpevaluator.service.AuditLogService;
import com.team02.spmpevaluator.service.ComplianceEvaluationService;
import com.team02.spmpevaluator.service.ComplianceHistoryService;
import com.team02.spmpevaluator.service.ReportExportService;
import com.team02.spmpevaluator.service.SPMPDocumentService;
import com.team02.spmpevaluator.service.UserService;
import com.team02.spmpevaluator.util.DocumentParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("DocumentController Integration Tests")
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SPMPDocumentService documentService;

    @MockBean
    private ComplianceEvaluationService evaluationService;

    @MockBean
    private DocumentParser documentParser;

    @MockBean
    private UserService userService;

    @MockBean
    private AuditLogService auditLogService;

    @MockBean
    private ComplianceScoreRepository complianceScoreRepository;

    @MockBean
    private ComplianceHistoryService complianceHistoryService;

    @MockBean
    private ReportExportService reportExportService;

    private User studentUser;
    private User professorUser;
    private SPMPDocument testDocument;
    private ComplianceScore testComplianceScore;
    private ComplianceReportDTO testReportDTO;

    @BeforeEach
    void setUp() {
        // Setup student user
        studentUser = new User();
        studentUser.setId(1L);
        studentUser.setUsername("student");
        studentUser.setEmail("student@test.com");
        studentUser.setRole(Role.STUDENT);

        // Setup professor user
        professorUser = new User();
        professorUser.setId(2L);
        professorUser.setUsername("professor");
        professorUser.setEmail("professor@test.com");
        professorUser.setRole(Role.PROFESSOR);

        // Setup test document
        testDocument = new SPMPDocument();
        testDocument.setId(1L);
        testDocument.setFileName("test-spmp.pdf");
        testDocument.setFileUrl("/uploads/test-spmp.pdf");
        testDocument.setFileSize(1024L);
        testDocument.setFileType("PDF");
        testDocument.setEvaluated(false);
        testDocument.setUploadedBy(studentUser);
        testDocument.setUploadedAt(LocalDateTime.now());

        // Setup compliance score
        testComplianceScore = new ComplianceScore();
        testComplianceScore.setId(1L);
        testComplianceScore.setOverallScore(85.0);
        testComplianceScore.setStructureScore(90.0);
        testComplianceScore.setCompletenessScore(80.0);
        testComplianceScore.setSectionsFound(12);
        testComplianceScore.setTotalSectionsRequired(15);
        testComplianceScore.setCompliant(true);
        testComplianceScore.setDocument(testDocument);

        // Setup report DTO
        testReportDTO = new ComplianceReportDTO();
        testReportDTO.setDocumentId(1L);
        testReportDTO.setDocumentName("test-spmp.pdf");
        testReportDTO.setOverallScore(85.0);
        testReportDTO.setStructureScore(90.0);
        testReportDTO.setCompletenessScore(80.0);
        testReportDTO.setSectionsFound(12);
        testReportDTO.setTotalSectionsRequired(15);
        testReportDTO.setCompliant(true);
        testReportDTO.setSummary("Document is compliant with IEEE 1058 standard.");
        testReportDTO.setSectionAnalyses(new ArrayList<>());
        testReportDTO.setEvaluatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("POST /api/documents/upload - Upload Document")
    class UploadDocumentTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should upload document successfully")
        void uploadDocument_Success() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test-spmp.pdf",
                    MediaType.APPLICATION_PDF_VALUE,
                    "PDF content".getBytes());

            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(documentService.uploadDocument(any(), eq(studentUser))).thenReturn(testDocument);

            mockMvc.perform(multipart("/api/documents/upload")
                    .file(file))
                    .andExpect(status().isCreated())
                    .andExpect(
                            content().string(org.hamcrest.Matchers.containsString("Document uploaded successfully")));

            verify(documentService).uploadDocument(any(), eq(studentUser));
        }

        @Test
        @WithMockUser(username = "unknown")
        @DisplayName("Should fail upload when user not found")
        void uploadDocument_UserNotFound() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test-spmp.pdf",
                    MediaType.APPLICATION_PDF_VALUE,
                    "PDF content".getBytes());

            when(userService.findByUsername("unknown")).thenReturn(Optional.empty());

            mockMvc.perform(multipart("/api/documents/upload")
                    .file(file))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("User not found")));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should fail upload with invalid file")
        void uploadDocument_InvalidFile() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test.txt",
                    MediaType.TEXT_PLAIN_VALUE,
                    "Invalid content".getBytes());

            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(documentService.uploadDocument(any(), eq(studentUser)))
                    .thenThrow(new IllegalArgumentException("Invalid file type"));

            mockMvc.perform(multipart("/api/documents/upload")
                    .file(file))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Invalid file type")));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should handle IOException during upload")
        void uploadDocument_IOException() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test-spmp.pdf",
                    MediaType.APPLICATION_PDF_VALUE,
                    "PDF content".getBytes());

            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(documentService.uploadDocument(any(), eq(studentUser)))
                    .thenThrow(new IOException("File processing error"));

            mockMvc.perform(multipart("/api/documents/upload")
                    .file(file))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to process file")));
        }

        @Test
        @DisplayName("Should require authentication for upload")
        void uploadDocument_RequiresAuth() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test-spmp.pdf",
                    MediaType.APPLICATION_PDF_VALUE,
                    "PDF content".getBytes());

            // Spring Security redirects to OAuth2 login (302) when not authenticated
            mockMvc.perform(multipart("/api/documents/upload")
                    .file(file))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("POST /api/documents/{documentId}/evaluate - Evaluate Document")
    class EvaluateDocumentTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should evaluate document successfully")
        void evaluateDocument_Success() throws Exception {
            testDocument.setEvaluated(false);

            when(documentService.getDocumentById(1L)).thenReturn(Optional.of(testDocument));
            when(documentService.getDocumentContent(1L)).thenReturn("Document content");
            when(evaluationService.evaluateDocument(eq(testDocument), anyString())).thenReturn(testComplianceScore);
            when(evaluationService.convertToDTO(any(), anyLong(), anyString())).thenReturn(testReportDTO);

            mockMvc.perform(post("/api/documents/1/evaluate"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.documentId").value(1))
                    .andExpect(jsonPath("$.overallScore").value(85.0));

            verify(documentService).updateDocumentEvaluation(eq(1L), anyString(), eq(true));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should fail evaluation when document not found")
        void evaluateDocument_NotFound() throws Exception {
            when(documentService.getDocumentById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/documents/999/evaluate"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Document not found")));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should handle IOException during evaluation")
        void evaluateDocument_IOException() throws Exception {
            when(documentService.getDocumentById(1L)).thenReturn(Optional.of(testDocument));
            when(documentService.getDocumentContent(1L)).thenThrow(new IOException("Failed to read document"));

            mockMvc.perform(post("/api/documents/1/evaluate"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to process document")));
        }
    }

    @Nested
    @DisplayName("GET /api/documents/my-documents - Get My Documents")
    class GetMyDocumentsTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should get user's documents successfully")
        void getMyDocuments_Success() throws Exception {
            List<SPMPDocument> documents = List.of(testDocument);

            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(documentService.getDocumentsByUser(studentUser.getId())).thenReturn(documents);

            mockMvc.perform(get("/api/documents/my-documents"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].fileName").value("test-spmp.pdf"));
        }

        @Test
        @WithMockUser(username = "unknown")
        @DisplayName("Should fail when user not found")
        void getMyDocuments_UserNotFound() throws Exception {
            when(userService.findByUsername("unknown")).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/documents/my-documents"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("User not found")));
        }
    }

    @Nested
    @DisplayName("GET /api/documents/{documentId} - Get Document")
    class GetDocumentTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should get own document as student")
        void getDocument_OwnDocument() throws Exception {
            when(documentService.getDocumentById(1L)).thenReturn(Optional.of(testDocument));
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));

            mockMvc.perform(get("/api/documents/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fileName").value("test-spmp.pdf"));
        }

        @Test
        @WithMockUser(username = "otherstudent")
        @DisplayName("Should forbid student from viewing other's document")
        void getDocument_OtherStudentDocument() throws Exception {
            User otherStudent = new User();
            otherStudent.setId(3L);
            otherStudent.setUsername("otherstudent");
            otherStudent.setRole(Role.STUDENT);

            when(documentService.getDocumentById(1L)).thenReturn(Optional.of(testDocument));
            when(userService.findByUsername("otherstudent")).thenReturn(Optional.of(otherStudent));

            mockMvc.perform(get("/api/documents/1"))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Unauthorized")));
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should allow professor to view any document")
        void getDocument_ProfessorAccess() throws Exception {
            when(documentService.getDocumentById(1L)).thenReturn(Optional.of(testDocument));
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));

            mockMvc.perform(get("/api/documents/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fileName").value("test-spmp.pdf"));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should return error when document not found")
        void getDocument_NotFound() throws Exception {
            when(documentService.getDocumentById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/documents/999"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Document not found")));
        }
    }

    @Nested
    @DisplayName("GET /api/documents/{documentId}/report - Get Evaluation Report")
    class GetEvaluationReportTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should get evaluation report successfully")
        void getEvaluationReport_Success() throws Exception {
            testDocument.setEvaluated(true);

            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(documentService.getDocumentById(1L)).thenReturn(Optional.of(testDocument));
            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testComplianceScore));
            when(evaluationService.convertToDTO(any(), anyLong(), anyString())).thenReturn(testReportDTO);

            mockMvc.perform(get("/api/documents/1/report"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.documentId").value(1))
                    .andExpect(jsonPath("$.overallScore").value(85.0));

            verify(auditLogService).logFeedbackView(anyLong(), eq(1L), anyString());
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should fail when document not evaluated")
        void getEvaluationReport_NotEvaluated() throws Exception {
            testDocument.setEvaluated(false);

            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(documentService.getDocumentById(1L)).thenReturn(Optional.of(testDocument));

            mockMvc.perform(get("/api/documents/1/report"))
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            content().string(org.hamcrest.Matchers.containsString("Document has not been evaluated")));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should return not found when no compliance score exists")
        void getEvaluationReport_NoComplianceScore() throws Exception {
            testDocument.setEvaluated(true);

            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(documentService.getDocumentById(1L)).thenReturn(Optional.of(testDocument));
            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.empty());

            mockMvc.perform(get("/api/documents/1/report"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/documents/{documentId} - Delete Document")
    class DeleteDocumentTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should delete document successfully")
        void deleteDocument_Success() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            doNothing().when(documentService).deleteDocument(1L, studentUser.getId());

            mockMvc.perform(delete("/api/documents/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Document deleted successfully"));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should fail when document not found")
        void deleteDocument_NotFound() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            doThrow(new IllegalArgumentException("Document not found"))
                    .when(documentService).deleteDocument(999L, studentUser.getId());

            mockMvc.perform(delete("/api/documents/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Document not found"));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should handle IOException during deletion")
        void deleteDocument_IOException() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            doThrow(new IOException("Failed to delete file"))
                    .when(documentService).deleteDocument(1L, studentUser.getId());

            mockMvc.perform(delete("/api/documents/1"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to delete document")));
        }
    }

    @Nested
    @DisplayName("GET /api/documents/evaluated - Get Evaluated Documents")
    class GetEvaluatedDocumentsTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should get evaluated documents successfully")
        void getEvaluatedDocuments_Success() throws Exception {
            testDocument.setEvaluated(true);
            List<SPMPDocument> documents = List.of(testDocument);

            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(documentService.getEvaluatedDocuments(studentUser.getId())).thenReturn(documents);

            mockMvc.perform(get("/api/documents/evaluated"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].fileName").value("test-spmp.pdf"));
        }
    }

    @Nested
    @DisplayName("GET /api/documents/pending - Get Pending Documents")
    class GetPendingDocumentsTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should get pending documents successfully")
        void getPendingDocuments_Success() throws Exception {
            testDocument.setEvaluated(false);
            List<SPMPDocument> documents = List.of(testDocument);

            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(documentService.getUnevaluatedDocuments(studentUser.getId())).thenReturn(documents);

            mockMvc.perform(get("/api/documents/pending"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].fileName").value("test-spmp.pdf"));
        }
    }

    @Nested
    @DisplayName("PUT /api/documents/{documentId}/notes - Add Notes")
    class AddNotesTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should add notes successfully")
        void addNotes_Success() throws Exception {
            testDocument.setNotes("Test notes");

            when(documentService.addNotes(1L, "Test notes")).thenReturn(testDocument);

            mockMvc.perform(put("/api/documents/1/notes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("Test notes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.notes").value("Test notes"));
        }
    }

    @Nested
    @DisplayName("PUT /api/documents/{documentId}/replace - Replace Document")
    class ReplaceDocumentTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should replace document successfully")
        void replaceDocument_Success() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "updated-spmp.pdf",
                    MediaType.APPLICATION_PDF_VALUE,
                    "Updated PDF content".getBytes());

            SPMPDocument updatedDocument = new SPMPDocument();
            updatedDocument.setId(1L);
            updatedDocument.setFileName("updated-spmp.pdf");
            updatedDocument.setUploadedBy(studentUser);

            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(documentService.getDocumentById(1L)).thenReturn(Optional.of(testDocument));
            when(documentService.replaceDocument(eq(1L), any(), eq(studentUser))).thenReturn(updatedDocument);

            mockMvc.perform(multipart("/api/documents/1/replace")
                    .file(file)
                    .with(request -> {
                        request.setMethod("PUT");
                        return request;
                    }))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fileName").value("updated-spmp.pdf"));
        }

        @Test
        @WithMockUser(username = "otherstudent")
        @DisplayName("Should forbid replacing another user's document")
        void replaceDocument_Forbidden() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "updated-spmp.pdf",
                    MediaType.APPLICATION_PDF_VALUE,
                    "Updated PDF content".getBytes());

            User otherStudent = new User();
            otherStudent.setId(3L);
            otherStudent.setUsername("otherstudent");
            otherStudent.setRole(Role.STUDENT);

            when(userService.findByUsername("otherstudent")).thenReturn(Optional.of(otherStudent));
            when(documentService.getDocumentById(1L)).thenReturn(Optional.of(testDocument));

            mockMvc.perform(multipart("/api/documents/1/replace")
                    .file(file)
                    .with(request -> {
                        request.setMethod("PUT");
                        return request;
                    }))
                    .andExpect(status().isForbidden())
                    .andExpect(content()
                            .string(org.hamcrest.Matchers.containsString("You can only replace your own documents")));
        }
    }

    @Nested
    @DisplayName("GET /api/documents/all-submissions - Get All Submissions")
    class GetAllSubmissionsTests {

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should get all submissions as professor")
        void getAllSubmissions_Success() throws Exception {
            List<SPMPDocument> submissions = List.of(testDocument);

            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(documentService.getAllSubmissions(null, null)).thenReturn(submissions);

            mockMvc.perform(get("/api/documents/all-submissions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].fileName").value("test-spmp.pdf"));
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should filter submissions by status")
        void getAllSubmissions_WithStatusFilter() throws Exception {
            List<SPMPDocument> submissions = List.of(testDocument);

            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(documentService.getAllSubmissions("evaluated", null)).thenReturn(submissions);

            mockMvc.perform(get("/api/documents/all-submissions")
                    .param("status", "evaluated"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].fileName").value("test-spmp.pdf"));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should forbid student from viewing all submissions")
        void getAllSubmissions_ForbiddenForStudent() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));

            mockMvc.perform(get("/api/documents/all-submissions"))
                    .andExpect(status().isForbidden())
                    .andExpect(content()
                            .string(org.hamcrest.Matchers.containsString("Only professors can view all submissions")));
        }
    }

    @Nested
    @DisplayName("PUT /api/documents/{documentId}/override-score - Override Score")
    class OverrideScoreTests {

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should override score successfully")
        void overrideScore_Success() throws Exception {
            testDocument.setComplianceScore(testComplianceScore);

            Map<String, Object> overrideData = new HashMap<>();
            overrideData.put("score", 90.0);
            overrideData.put("notes", "Good work!");

            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(documentService.overrideScore(eq(1L), eq(90.0), eq("Good work!"), eq(professorUser)))
                    .thenReturn(testDocument);

            mockMvc.perform(put("/api/documents/1/override-score")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(overrideData)))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should forbid student from overriding score")
        void overrideScore_ForbiddenForStudent() throws Exception {
            Map<String, Object> overrideData = new HashMap<>();
            overrideData.put("score", 90.0);
            overrideData.put("notes", "Override attempt");

            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));

            mockMvc.perform(put("/api/documents/1/override-score")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(overrideData)))
                    .andExpect(status().isForbidden())
                    .andExpect(content()
                            .string(org.hamcrest.Matchers.containsString("Only professors can override scores")));
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should reject invalid score range")
        void overrideScore_InvalidScore() throws Exception {
            Map<String, Object> overrideData = new HashMap<>();
            overrideData.put("score", 150.0);
            overrideData.put("notes", "Invalid score");

            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));

            mockMvc.perform(put("/api/documents/1/override-score")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(overrideData)))
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            content().string(org.hamcrest.Matchers.containsString("Score must be between 0 and 100")));
        }
    }

    @Nested
    @DisplayName("POST /api/documents/{documentId}/re-evaluate - Re-evaluate Document")
    class ReEvaluateDocumentTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should re-evaluate document successfully")
        void reEvaluateDocument_Success() throws Exception {
            testDocument.setEvaluated(true);

            when(documentService.getDocumentById(1L)).thenReturn(Optional.of(testDocument));
            when(complianceScoreRepository.findByDocumentIdWithDocument(1L))
                    .thenReturn(Optional.of(testComplianceScore));
            when(documentService.getDocumentContent(1L)).thenReturn("Document content");
            when(evaluationService.evaluateDocument(eq(testDocument), anyString()))
                    .thenReturn(testComplianceScore);
            when(evaluationService.convertToDTO(any(), anyLong(), anyString())).thenReturn(testReportDTO);

            mockMvc.perform(post("/api/documents/1/re-evaluate"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.documentId").value(1))
                    .andExpect(jsonPath("$.overallScore").value(85.0));

            verify(complianceHistoryService).archiveScore(any(), eq("RE_EVALUATION"), any());
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should re-evaluate document without existing score")
        void reEvaluateDocument_NoExistingScore() throws Exception {
            testDocument.setEvaluated(false);

            when(documentService.getDocumentById(1L)).thenReturn(Optional.of(testDocument));
            when(complianceScoreRepository.findByDocumentIdWithDocument(1L))
                    .thenReturn(Optional.empty());
            when(documentService.getDocumentContent(1L)).thenReturn("Document content");
            when(evaluationService.evaluateDocument(eq(testDocument), anyString()))
                    .thenReturn(testComplianceScore);
            when(evaluationService.convertToDTO(any(), anyLong(), anyString())).thenReturn(testReportDTO);

            mockMvc.perform(post("/api/documents/1/re-evaluate"))
                    .andExpect(status().isOk());

            verify(complianceHistoryService, never()).archiveScore(any(), anyString(), any());
        }
    }

    @Nested
    @DisplayName("GET /api/documents/{documentId}/history - Get Score History")
    class GetScoreHistoryTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should get score history successfully")
        void getScoreHistory_Success() throws Exception {
            ComplianceScoreHistory historyEntry = new ComplianceScoreHistory();
            historyEntry.setId(1L);
            historyEntry.setOverallScore(85.0);
            historyEntry.setStructureScore(90.0);
            historyEntry.setCompletenessScore(80.0);
            historyEntry.setSectionsFound(12);
            historyEntry.setTotalSectionsRequired(15);
            historyEntry.setCompliant(true);
            historyEntry.setVersionNumber(1);
            historyEntry.setSource("EVALUATION");
            historyEntry.setEvaluatedAt(LocalDateTime.now());
            historyEntry.setRecordedAt(LocalDateTime.now());

            List<ComplianceScoreHistory> history = List.of(historyEntry);

            when(complianceHistoryService.getHistoryForDocument(1L)).thenReturn(history);

            mockMvc.perform(get("/api/documents/1/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].overallScore").value(85.0))
                    .andExpect(jsonPath("$[0].versionNumber").value(1));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should return empty history when none exists")
        void getScoreHistory_Empty() throws Exception {
            when(complianceHistoryService.getHistoryForDocument(1L)).thenReturn(new ArrayList<>());

            mockMvc.perform(get("/api/documents/1/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/documents/{documentId}/export/pdf - Export PDF")
    class ExportPdfTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should export PDF successfully")
        void exportPdf_Success() throws Exception {
            byte[] pdfContent = "PDF content bytes".getBytes();

            when(reportExportService.exportPdf(1L)).thenReturn(pdfContent);

            mockMvc.perform(get("/api/documents/1/export/pdf"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition", "attachment; filename=spmp-report-1.pdf"))
                    .andExpect(header().string("Content-Type", "application/pdf"));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should handle export failure")
        void exportPdf_Failure() throws Exception {
            when(reportExportService.exportPdf(1L)).thenThrow(new RuntimeException("Export failed"));

            mockMvc.perform(get("/api/documents/1/export/pdf"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to export PDF")));
        }
    }

    @Nested
    @DisplayName("GET /api/documents/{documentId}/export/excel - Export Excel")
    class ExportExcelTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should export Excel successfully")
        void exportExcel_Success() throws Exception {
            byte[] excelContent = "Excel content bytes".getBytes();

            when(reportExportService.exportExcel(1L)).thenReturn(excelContent);

            mockMvc.perform(get("/api/documents/1/export/excel"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition", "attachment; filename=spmp-report-1.xlsx"))
                    .andExpect(header().string("Content-Type",
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should handle export failure")
        void exportExcel_Failure() throws Exception {
            when(reportExportService.exportExcel(1L)).thenThrow(new RuntimeException("Export failed"));

            mockMvc.perform(get("/api/documents/1/export/excel"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to export Excel")));
        }
    }
}
