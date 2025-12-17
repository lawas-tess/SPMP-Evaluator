package com.team02.spmpevaluator.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SPMPDocument entity.
 */
@DisplayName("SPMPDocument Entity Tests")
class SPMPDocumentTest {

    private SPMPDocument document;
    private User user;

    @BeforeEach
    void setUp() {
        document = new SPMPDocument();
        user = new User();
        user.setId(1L);
        user.setUsername("student");
        user.setRole(Role.STUDENT);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty entity with no-args constructor")
        void noArgsConstructor_CreatesEmptyEntity() {
            SPMPDocument entity = new SPMPDocument();
            assertNotNull(entity);
            assertNull(entity.getId());
            assertNull(entity.getFileName());
            assertFalse(entity.isEvaluated());
        }

        @Test
        @DisplayName("Should create entity with all-args constructor")
        void allArgsConstructor_CreatesPopulatedEntity() {
            LocalDateTime now = LocalDateTime.now();
            ComplianceScore score = new ComplianceScore();
            List<ComplianceScoreHistory> history = new ArrayList<>();

            SPMPDocument entity = new SPMPDocument(
                    1L, "Project_SPMP.pdf", "/uploads/documents/1.pdf",
                    1024L, "PDF", true, "Good document structure",
                    user, now, now, now, score, history, "Initial submission");

            assertEquals(1L, entity.getId());
            assertEquals("Project_SPMP.pdf", entity.getFileName());
            assertEquals("/uploads/documents/1.pdf", entity.getFileUrl());
            assertEquals(1024L, entity.getFileSize());
            assertEquals("PDF", entity.getFileType());
            assertTrue(entity.isEvaluated());
            assertEquals("Good document structure", entity.getFeedback());
            assertEquals(user, entity.getUploadedBy());
            assertEquals("Initial submission", entity.getNotes());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void testId() {
            document.setId(100L);
            assertEquals(100L, document.getId());
        }

        @Test
        @DisplayName("Should set and get fileName")
        void testFileName() {
            document.setFileName("Team02_SPMP_Final.pdf");
            assertEquals("Team02_SPMP_Final.pdf", document.getFileName());
        }

        @Test
        @DisplayName("Should set and get fileUrl")
        void testFileUrl() {
            document.setFileUrl("/uploads/documents/100.pdf");
            assertEquals("/uploads/documents/100.pdf", document.getFileUrl());
        }

        @Test
        @DisplayName("Should set and get fileSize")
        void testFileSize() {
            document.setFileSize(2048L);
            assertEquals(2048L, document.getFileSize());
        }

        @Test
        @DisplayName("Should set and get fileType")
        void testFileType() {
            document.setFileType("DOCX");
            assertEquals("DOCX", document.getFileType());
        }

        @Test
        @DisplayName("Should set and get evaluated")
        void testEvaluated() {
            document.setEvaluated(true);
            assertTrue(document.isEvaluated());
            document.setEvaluated(false);
            assertFalse(document.isEvaluated());
        }

        @Test
        @DisplayName("Should set and get feedback")
        void testFeedback() {
            document.setFeedback("Document meets IEEE 1058 standards");
            assertEquals("Document meets IEEE 1058 standards", document.getFeedback());
        }

        @Test
        @DisplayName("Should set and get uploadedBy")
        void testUploadedBy() {
            document.setUploadedBy(user);
            assertEquals(user, document.getUploadedBy());
        }

        @Test
        @DisplayName("Should set and get uploadedAt")
        void testUploadedAt() {
            LocalDateTime now = LocalDateTime.now();
            document.setUploadedAt(now);
            assertEquals(now, document.getUploadedAt());
        }

        @Test
        @DisplayName("Should set and get evaluatedAt")
        void testEvaluatedAt() {
            LocalDateTime now = LocalDateTime.now();
            document.setEvaluatedAt(now);
            assertEquals(now, document.getEvaluatedAt());
        }

        @Test
        @DisplayName("Should set and get updatedAt")
        void testUpdatedAt() {
            LocalDateTime now = LocalDateTime.now();
            document.setUpdatedAt(now);
            assertEquals(now, document.getUpdatedAt());
        }

        @Test
        @DisplayName("Should set and get complianceScore")
        void testComplianceScore() {
            ComplianceScore score = new ComplianceScore();
            score.setId(1L);
            document.setComplianceScore(score);
            assertEquals(score, document.getComplianceScore());
        }

        @Test
        @DisplayName("Should set and get scoreHistory")
        void testScoreHistory() {
            List<ComplianceScoreHistory> history = new ArrayList<>();
            ComplianceScoreHistory h1 = new ComplianceScoreHistory();
            h1.setId(1L);
            history.add(h1);
            document.setScoreHistory(history);
            assertEquals(1, document.getScoreHistory().size());
        }

        @Test
        @DisplayName("Should set and get notes")
        void testNotes() {
            document.setNotes("This is a revised submission");
            assertEquals("This is a revised submission", document.getNotes());
        }
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("Should have evaluated default to false")
        void evaluatedDefaultFalse() {
            SPMPDocument entity = new SPMPDocument();
            assertFalse(entity.isEvaluated());
        }
    }

    @Nested
    @DisplayName("File Type Tests")
    class FileTypeTests {

        @Test
        @DisplayName("Should accept PDF file type")
        void pdfFileType() {
            document.setFileType("PDF");
            assertEquals("PDF", document.getFileType());
        }

        @Test
        @DisplayName("Should accept DOCX file type")
        void docxFileType() {
            document.setFileType("DOCX");
            assertEquals("DOCX", document.getFileType());
        }

        @Test
        @DisplayName("Should accept DOC file type")
        void docFileType() {
            document.setFileType("DOC");
            assertEquals("DOC", document.getFileType());
        }
    }

    @Nested
    @DisplayName("Document Upload Workflow Tests")
    class DocumentUploadWorkflowTests {

        @Test
        @DisplayName("Should represent initial upload state (UC 2.1)")
        void initialUploadState() {
            document.setFileName("SPMP_Draft.pdf");
            document.setFileUrl("/uploads/documents/draft.pdf");
            document.setUploadedBy(user);
            document.setUploadedAt(LocalDateTime.now());
            document.setEvaluated(false);

            assertNotNull(document.getFileName());
            assertNotNull(document.getFileUrl());
            assertNotNull(document.getUploadedBy());
            assertFalse(document.isEvaluated());
            assertNull(document.getComplianceScore());
        }

        @Test
        @DisplayName("Should represent evaluated state (UC 2.2)")
        void evaluatedState() {
            document.setFileName("SPMP_Final.pdf");
            document.setEvaluated(true);
            document.setEvaluatedAt(LocalDateTime.now());
            document.setFeedback("Comprehensive SPMP document with all required sections.");

            ComplianceScore score = new ComplianceScore();
            score.setOverallScore(85.0);
            document.setComplianceScore(score);

            assertTrue(document.isEvaluated());
            assertNotNull(document.getEvaluatedAt());
            assertNotNull(document.getFeedback());
            assertNotNull(document.getComplianceScore());
        }
    }

    @Nested
    @DisplayName("Score History Tests")
    class ScoreHistoryTests {

        @Test
        @DisplayName("Should track multiple evaluations")
        void multipleEvaluations() {
            List<ComplianceScoreHistory> history = new ArrayList<>();

            ComplianceScoreHistory h1 = new ComplianceScoreHistory();
            h1.setVersionNumber(1);
            h1.setOverallScore(70.0);
            history.add(h1);

            ComplianceScoreHistory h2 = new ComplianceScoreHistory();
            h2.setVersionNumber(2);
            h2.setOverallScore(85.0);
            history.add(h2);

            document.setScoreHistory(history);

            assertEquals(2, document.getScoreHistory().size());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when same id")
        void equalsWithSameId() {
            SPMPDocument doc1 = new SPMPDocument();
            doc1.setId(1L);
            doc1.setFileName("doc1.pdf");

            SPMPDocument doc2 = new SPMPDocument();
            doc2.setId(1L);
            doc2.setFileName("doc1.pdf");

            assertEquals(doc1, doc2);
            assertEquals(doc1.hashCode(), doc2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different ids")
        void notEqualsWithDifferentIds() {
            SPMPDocument doc1 = new SPMPDocument();
            doc1.setId(1L);

            SPMPDocument doc2 = new SPMPDocument();
            doc2.setId(2L);

            assertNotEquals(doc1, doc2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include key fields in toString")
        void toStringContainsFields() {
            document.setId(1L);
            document.setFileName("Test_SPMP.pdf");
            document.setEvaluated(true);

            String str = document.toString();
            assertNotNull(str);
            assertTrue(str.contains("1"));
        }
    }
}
