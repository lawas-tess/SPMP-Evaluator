package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.entity.ComplianceScore;
import com.team02.spmpevaluator.entity.SPMPDocument;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.ComplianceScoreHistoryRepository;
import com.team02.spmpevaluator.repository.ComplianceScoreRepository;
import com.team02.spmpevaluator.repository.SPMPDocumentRepository;
import com.team02.spmpevaluator.util.DocumentParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SPMPDocumentService.
 * Tests document upload, retrieval, deletion, and score override functionality.
 */
@ExtendWith(MockitoExtension.class)
class SPMPDocumentServiceTest {

    @Mock
    private SPMPDocumentRepository repository;

    @Mock
    private DocumentParser documentParser;

    @Mock
    private ComplianceScoreRepository complianceScoreRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ComplianceHistoryService complianceHistoryService;

    @Mock
    private ComplianceScoreHistoryRepository historyRepository;

    @InjectMocks
    private SPMPDocumentService documentService;

    private User testUser;
    private User testProfessor;
    private SPMPDocument testDocument;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("student@test.com");

        testProfessor = new User();
        testProfessor.setId(2L);
        testProfessor.setEmail("professor@test.com");

        testDocument = new SPMPDocument();
        testDocument.setId(1L);
        testDocument.setFileName("test_spmp.pdf");
        testDocument.setFileUrl("/uploads/documents/test_spmp.pdf");
        testDocument.setFileSize(1024L);
        testDocument.setFileType("PDF");
        testDocument.setUploadedBy(testUser);
        testDocument.setEvaluated(false);
    }

    @Nested
    @DisplayName("Upload Document Tests")
    class UploadDocumentTests {

        @Test
        @DisplayName("Should reject empty file")
        void uploadDocument_EmptyFile_ThrowsException() {
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "file", "test.pdf", "application/pdf", new byte[0]);

            assertThrows(IllegalArgumentException.class, () -> documentService.uploadDocument(emptyFile, testUser));
        }

        @Test
        @DisplayName("Should reject file exceeding max size")
        void uploadDocument_FileTooLarge_ThrowsException() {
            byte[] largeContent = new byte[51 * 1024 * 1024]; // 51MB
            MockMultipartFile largeFile = new MockMultipartFile(
                    "file", "test.pdf", "application/pdf", largeContent);

            assertThrows(IllegalArgumentException.class, () -> documentService.uploadDocument(largeFile, testUser));
        }

        @Test
        @DisplayName("Should reject unsupported file types")
        void uploadDocument_UnsupportedFileType_ThrowsException() {
            MockMultipartFile txtFile = new MockMultipartFile(
                    "file", "test.txt", "text/plain", "content".getBytes());

            assertThrows(IllegalArgumentException.class, () -> documentService.uploadDocument(txtFile, testUser));
        }

        @Test
        @DisplayName("Should reject null filename")
        void uploadDocument_NullFilename_ThrowsException() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", null, "application/pdf", "content".getBytes());

            assertThrows(IllegalArgumentException.class, () -> documentService.uploadDocument(file, testUser));
        }
    }

    @Nested
    @DisplayName("Get Document by ID Tests")
    class GetDocumentByIdTests {

        @Test
        @DisplayName("Should return document when found")
        void getDocumentById_ExistingDocument_ReturnsDocument() {
            when(repository.findByIdWithUploadedBy(1L)).thenReturn(Optional.of(testDocument));

            Optional<SPMPDocument> result = documentService.getDocumentById(1L);

            assertTrue(result.isPresent());
            assertEquals(testDocument.getId(), result.get().getId());
            assertEquals("test_spmp.pdf", result.get().getFileName());
        }

        @Test
        @DisplayName("Should return empty when document not found")
        void getDocumentById_NonExistentDocument_ReturnsEmpty() {
            when(repository.findByIdWithUploadedBy(999L)).thenReturn(Optional.empty());

            Optional<SPMPDocument> result = documentService.getDocumentById(999L);

            assertFalse(result.isPresent());
        }
    }

    @Nested
    @DisplayName("Get Documents by User Tests")
    class GetDocumentsByUserTests {

        @Test
        @DisplayName("Should return user documents")
        void getDocumentsByUser_ExistingUser_ReturnsDocuments() {
            List<SPMPDocument> documents = Arrays.asList(testDocument);
            when(repository.findByUploadedBy_Id(1L)).thenReturn(documents);

            List<SPMPDocument> result = documentService.getDocumentsByUser(1L);

            assertEquals(1, result.size());
            assertEquals(testDocument.getId(), result.get(0).getId());
        }

        @Test
        @DisplayName("Should return empty list for user with no documents")
        void getDocumentsByUser_NoDocuments_ReturnsEmptyList() {
            when(repository.findByUploadedBy_Id(999L)).thenReturn(List.of());

            List<SPMPDocument> result = documentService.getDocumentsByUser(999L);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Get All Documents Tests")
    class GetAllDocumentsTests {

        @Test
        @DisplayName("Should return paginated documents")
        void getAllDocuments_WithPagination_ReturnsPaginatedDocuments() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<SPMPDocument> page = new PageImpl<>(Arrays.asList(testDocument), pageable, 1);
            when(repository.findAll(pageable)).thenReturn(page);

            Page<SPMPDocument> result = documentService.getAllDocuments(pageable);

            assertEquals(1, result.getTotalElements());
            assertEquals(testDocument.getId(), result.getContent().get(0).getId());
        }

        @Test
        @DisplayName("Should return all documents without pagination")
        void getAllDocuments_NoPagination_ReturnsAllDocuments() {
            when(repository.findAll()).thenReturn(Arrays.asList(testDocument));

            List<SPMPDocument> result = documentService.getAllDocuments();

            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("Get Evaluated/Unevaluated Documents Tests")
    class GetEvaluatedDocumentsTests {

        @Test
        @DisplayName("Should return evaluated documents")
        void getEvaluatedDocuments_ReturnsEvaluatedOnly() {
            testDocument.setEvaluated(true);
            when(repository.findByUploadedBy_IdAndEvaluated(1L, true))
                    .thenReturn(Arrays.asList(testDocument));

            List<SPMPDocument> result = documentService.getEvaluatedDocuments(1L);

            assertEquals(1, result.size());
            assertTrue(result.get(0).isEvaluated());
        }

        @Test
        @DisplayName("Should return unevaluated documents")
        void getUnevaluatedDocuments_ReturnsUnevaluatedOnly() {
            when(repository.findByUploadedBy_IdAndEvaluated(1L, false))
                    .thenReturn(Arrays.asList(testDocument));

            List<SPMPDocument> result = documentService.getUnevaluatedDocuments(1L);

            assertEquals(1, result.size());
            assertFalse(result.get(0).isEvaluated());
        }
    }

    @Nested
    @DisplayName("Update Document Evaluation Tests")
    class UpdateDocumentEvaluationTests {

        @Test
        @DisplayName("Should update evaluation status and feedback")
        void updateDocumentEvaluation_ValidDocument_UpdatesDocument() {
            when(repository.findById(1L)).thenReturn(Optional.of(testDocument));
            when(repository.save(any(SPMPDocument.class))).thenReturn(testDocument);

            SPMPDocument result = documentService.updateDocumentEvaluation(1L, "Good compliance", true);

            assertTrue(result.isEvaluated());
            assertEquals("Good compliance", result.getFeedback());
            assertNotNull(result.getEvaluatedAt());
        }

        @Test
        @DisplayName("Should throw exception when document not found")
        void updateDocumentEvaluation_DocumentNotFound_ThrowsException() {
            when(repository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> documentService.updateDocumentEvaluation(999L, "feedback", true));
        }
    }

    @Nested
    @DisplayName("Add Notes Tests")
    class AddNotesTests {

        @Test
        @DisplayName("Should add notes to document")
        void addNotes_ValidDocument_AddsNotes() {
            when(repository.findById(1L)).thenReturn(Optional.of(testDocument));
            when(repository.save(any(SPMPDocument.class))).thenReturn(testDocument);

            SPMPDocument result = documentService.addNotes(1L, "Important notes");

            assertEquals("Important notes", result.getNotes());
            assertNotNull(result.getUpdatedAt());
        }

        @Test
        @DisplayName("Should throw exception when document not found")
        void addNotes_DocumentNotFound_ThrowsException() {
            when(repository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> documentService.addNotes(999L, "notes"));
        }
    }

    @Nested
    @DisplayName("Get Document Content Tests")
    class GetDocumentContentTests {

        @Test
        @DisplayName("Should extract content from PDF document")
        void getDocumentContent_PdfDocument_ExtractsContent() throws IOException {
            testDocument.setFileName("test.pdf");
            when(repository.findByIdWithUploadedBy(1L)).thenReturn(Optional.of(testDocument));
            when(documentParser.extractTextFromFile(anyString()))
                    .thenReturn("Extracted PDF content");

            String content = documentService.getDocumentContent(1L);

            assertEquals("Extracted PDF content", content);
            verify(documentParser).extractTextFromFile(testDocument.getFileUrl());
        }

        @Test
        @DisplayName("Should extract content from DOCX document")
        void getDocumentContent_DocxDocument_ExtractsContent() throws IOException {
            testDocument.setFileName("test.docx");
            when(repository.findByIdWithUploadedBy(1L)).thenReturn(Optional.of(testDocument));
            when(documentParser.extractTextFromFile(anyString()))
                    .thenReturn("Extracted DOCX content");

            String content = documentService.getDocumentContent(1L);

            assertEquals("Extracted DOCX content", content);
        }

        @Test
        @DisplayName("Should throw exception when document not found")
        void getDocumentContent_DocumentNotFound_ThrowsException() {
            when(repository.findByIdWithUploadedBy(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> documentService.getDocumentContent(999L));
        }
    }

    @Nested
    @DisplayName("Get All Submissions Tests (UC 2.7)")
    class GetAllSubmissionsTests {

        @Test
        @DisplayName("Should return all submissions without filters")
        void getAllSubmissions_NoFilters_ReturnsAll() {
            when(repository.findAll()).thenReturn(Arrays.asList(testDocument));

            List<SPMPDocument> result = documentService.getAllSubmissions(null, null);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should filter by evaluated status")
        void getAllSubmissions_FilterByEvaluated_ReturnsEvaluatedOnly() {
            testDocument.setEvaluated(true);
            SPMPDocument pendingDoc = new SPMPDocument();
            pendingDoc.setId(2L);
            pendingDoc.setUploadedBy(testUser);
            pendingDoc.setEvaluated(false);

            when(repository.findAll()).thenReturn(Arrays.asList(testDocument, pendingDoc));

            List<SPMPDocument> result = documentService.getAllSubmissions("evaluated", null);

            assertEquals(1, result.size());
            assertTrue(result.get(0).isEvaluated());
        }

        @Test
        @DisplayName("Should filter by pending status")
        void getAllSubmissions_FilterByPending_ReturnsPendingOnly() {
            testDocument.setEvaluated(false);
            SPMPDocument evaluatedDoc = new SPMPDocument();
            evaluatedDoc.setId(2L);
            evaluatedDoc.setUploadedBy(testUser);
            evaluatedDoc.setEvaluated(true);

            when(repository.findAll()).thenReturn(Arrays.asList(testDocument, evaluatedDoc));

            List<SPMPDocument> result = documentService.getAllSubmissions("pending", null);

            assertEquals(1, result.size());
            assertFalse(result.get(0).isEvaluated());
        }

        @Test
        @DisplayName("Should filter by student ID")
        void getAllSubmissions_FilterByStudent_ReturnsStudentDocsOnly() {
            SPMPDocument otherUserDoc = new SPMPDocument();
            otherUserDoc.setId(2L);
            User otherUser = new User();
            otherUser.setId(999L);
            otherUserDoc.setUploadedBy(otherUser);

            when(repository.findAll()).thenReturn(Arrays.asList(testDocument, otherUserDoc));

            List<SPMPDocument> result = documentService.getAllSubmissions(null, 1L);

            assertEquals(1, result.size());
            assertEquals(1L, result.get(0).getUploadedBy().getId());
        }
    }

    @Nested
    @DisplayName("Override Score Tests (UC 2.8)")
    class OverrideScoreTests {

        @BeforeEach
        void setUpComplianceScore() {
            ComplianceScore score = new ComplianceScore();
            score.setId(1L);
            score.setOverallScore(75.0);
            score.setDocument(testDocument);
            testDocument.setComplianceScore(score);
            testDocument.setEvaluated(true);
        }

        @Test
        @DisplayName("Should override score successfully")
        void overrideScore_ValidDocument_OverridesScore() {
            when(repository.findByIdWithUploadedBy(1L)).thenReturn(Optional.of(testDocument));
            when(complianceScoreRepository.save(any(ComplianceScore.class)))
                    .thenReturn(testDocument.getComplianceScore());

            SPMPDocument result = documentService.overrideScore(1L, 90.0, "Excellent work", testProfessor);

            ComplianceScore score = result.getComplianceScore();
            assertEquals(90.0, score.getProfessorOverride());
            assertEquals("Excellent work", score.getProfessorNotes());
            assertEquals(testProfessor, score.getReviewedBy());
            assertNotNull(score.getReviewedAt());
        }

        @Test
        @DisplayName("Should archive score before override")
        void overrideScore_ValidDocument_ArchivesScore() {
            when(repository.findByIdWithUploadedBy(1L)).thenReturn(Optional.of(testDocument));

            documentService.overrideScore(1L, 90.0, "notes", testProfessor);

            verify(complianceHistoryService).archiveScore(
                    any(ComplianceScore.class), eq("OVERRIDE"), eq(testProfessor.getId()));
        }

        @Test
        @DisplayName("Should notify student of score override")
        void overrideScore_ValidDocument_NotifiesStudent() {
            when(repository.findByIdWithUploadedBy(1L)).thenReturn(Optional.of(testDocument));

            documentService.overrideScore(1L, 90.0, "Good improvement", testProfessor);

            verify(notificationService).notifyScoreOverride(
                    testUser.getId(), 1L, 90.0, "Good improvement");
        }

        @Test
        @DisplayName("Should throw exception when document not found")
        void overrideScore_DocumentNotFound_ThrowsException() {
            when(repository.findByIdWithUploadedBy(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> documentService.overrideScore(999L, 90.0, "notes", testProfessor));
        }

        @Test
        @DisplayName("Should throw exception when document not evaluated")
        void overrideScore_DocumentNotEvaluated_ThrowsException() {
            testDocument.setEvaluated(false);
            testDocument.setComplianceScore(null);
            when(repository.findByIdWithUploadedBy(1L)).thenReturn(Optional.of(testDocument));

            assertThrows(IllegalArgumentException.class,
                    () -> documentService.overrideScore(1L, 90.0, "notes", testProfessor));
        }
    }

    @Nested
    @DisplayName("Delete Document Tests")
    class DeleteDocumentTests {

        @Test
        @DisplayName("Should throw exception when document not found")
        void deleteDocument_DocumentNotFound_ThrowsException() {
            when(repository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> documentService.deleteDocument(999L, 1L));
        }

        @Test
        @DisplayName("Should delete history entries before document")
        void deleteDocument_ExistingDocument_DeletesHistory() throws IOException {
            when(repository.findById(1L)).thenReturn(Optional.of(testDocument));

            try {
                documentService.deleteDocument(1L, 1L);
            } catch (Exception e) {
                // File deletion may fail in test environment
            }

            verify(historyRepository).deleteByDocumentId(1L);
        }
    }

    @Nested
    @DisplayName("Replace Document Tests (UC 2.2)")
    class ReplaceDocumentTests {

        @Test
        @DisplayName("Should reject empty file on replace")
        void replaceDocument_EmptyFile_ThrowsException() {
            when(repository.findByIdWithUploadedBy(1L)).thenReturn(Optional.of(testDocument));
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "file", "test.pdf", "application/pdf", new byte[0]);

            assertThrows(IllegalArgumentException.class,
                    () -> documentService.replaceDocument(1L, emptyFile, testUser));
        }

        @Test
        @DisplayName("Should reject file exceeding max size on replace")
        void replaceDocument_FileTooLarge_ThrowsException() {
            when(repository.findByIdWithUploadedBy(1L)).thenReturn(Optional.of(testDocument));
            byte[] largeContent = new byte[51 * 1024 * 1024];
            MockMultipartFile largeFile = new MockMultipartFile(
                    "file", "test.pdf", "application/pdf", largeContent);

            assertThrows(IllegalArgumentException.class,
                    () -> documentService.replaceDocument(1L, largeFile, testUser));
        }

        @Test
        @DisplayName("Should reject unsupported file type on replace")
        void replaceDocument_UnsupportedFileType_ThrowsException() {
            when(repository.findByIdWithUploadedBy(1L)).thenReturn(Optional.of(testDocument));
            MockMultipartFile txtFile = new MockMultipartFile(
                    "file", "test.txt", "text/plain", "content".getBytes());

            assertThrows(IllegalArgumentException.class, () -> documentService.replaceDocument(1L, txtFile, testUser));
        }

        @Test
        @DisplayName("Should throw exception when document not found on replace")
        void replaceDocument_DocumentNotFound_ThrowsException() {
            when(repository.findByIdWithUploadedBy(999L)).thenReturn(Optional.empty());
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.pdf", "application/pdf", "content".getBytes());

            assertThrows(IllegalArgumentException.class, () -> documentService.replaceDocument(999L, file, testUser));
        }
    }
}
