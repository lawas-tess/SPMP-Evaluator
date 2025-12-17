package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.entity.*;
import com.team02.spmpevaluator.repository.ComplianceScoreHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ComplianceHistoryService.
 * Tests compliance score history archiving and retrieval.
 */
@ExtendWith(MockitoExtension.class)
class ComplianceHistoryServiceTest {

    @Mock
    private ComplianceScoreHistoryRepository historyRepository;

    @InjectMocks
    private ComplianceHistoryService complianceHistoryService;

    private SPMPDocument testDocument;
    private ComplianceScore testComplianceScore;
    private ComplianceScoreHistory testHistory;
    private User testStudent;

    @BeforeEach
    void setUp() {
        // Setup test student
        testStudent = new User();
        testStudent.setId(1L);
        testStudent.setUsername("student1");
        testStudent.setEmail("student@example.com");
        testStudent.setRole(Role.STUDENT);
        testStudent.setEnabled(true);

        // Setup test document
        testDocument = new SPMPDocument();
        testDocument.setId(1L);
        testDocument.setFileName("test_spmp.pdf");
        testDocument.setFileUrl("/uploads/documents/test_spmp.pdf");
        testDocument.setUploadedBy(testStudent);
        testDocument.setUploadedAt(LocalDateTime.now());

        // Setup test compliance score
        testComplianceScore = new ComplianceScore();
        testComplianceScore.setId(1L);
        testComplianceScore.setDocument(testDocument);
        testComplianceScore.setOverallScore(85.0);
        testComplianceScore.setStructureScore(90.0);
        testComplianceScore.setCompletenessScore(80.0);
        testComplianceScore.setSectionsFound(10);
        testComplianceScore.setTotalSectionsRequired(12);
        testComplianceScore.setCompliant(true);
        testComplianceScore.setSummary("Document meets IEEE 1058 compliance threshold");
        testComplianceScore.setEvaluatedAt(LocalDateTime.now().minusDays(1));
        testComplianceScore.setProfessorOverride(null);
        testComplianceScore.setProfessorNotes(null);

        // Setup test history record
        testHistory = new ComplianceScoreHistory();
        testHistory.setId(1L);
        testHistory.setDocument(testDocument);
        testHistory.setOverallScore(75.0);
        testHistory.setStructureScore(80.0);
        testHistory.setCompletenessScore(70.0);
        testHistory.setSectionsFound(8);
        testHistory.setTotalSectionsRequired(12);
        testHistory.setCompliant(false);
        testHistory.setSummary("Initial evaluation");
        testHistory.setVersionNumber(1);
        testHistory.setSource("RE_EVALUATION");
        testHistory.setRecordedAt(LocalDateTime.now().minusDays(2));
    }

    @Nested
    @DisplayName("Archive Score Tests")
    class ArchiveScoreTests {

        @Test
        @DisplayName("Should archive compliance score successfully")
        void archiveScore_Success() {
            // Arrange
            when(historyRepository.countByDocumentId(1L)).thenReturn(0);
            when(historyRepository.save(any(ComplianceScoreHistory.class))).thenAnswer(invocation -> {
                ComplianceScoreHistory history = invocation.getArgument(0);
                history.setId(1L);
                return history;
            });

            // Act
            complianceHistoryService.archiveScore(testComplianceScore, "RE_EVALUATION", 2L);

            // Assert
            ArgumentCaptor<ComplianceScoreHistory> captor = ArgumentCaptor.forClass(ComplianceScoreHistory.class);
            verify(historyRepository).save(captor.capture());

            ComplianceScoreHistory savedHistory = captor.getValue();
            assertEquals(testDocument, savedHistory.getDocument());
            assertEquals(85.0, savedHistory.getOverallScore());
            assertEquals(90.0, savedHistory.getStructureScore());
            assertEquals(80.0, savedHistory.getCompletenessScore());
            assertEquals(10, savedHistory.getSectionsFound());
            assertEquals(12, savedHistory.getTotalSectionsRequired());
            assertTrue(savedHistory.isCompliant());
            assertEquals("RE_EVALUATION", savedHistory.getSource());
            assertEquals(2L, savedHistory.getRecordedByUserId());
            assertEquals(1, savedHistory.getVersionNumber()); // First version
        }

        @Test
        @DisplayName("Should increment version number correctly")
        void archiveScore_IncrementVersion() {
            // Arrange
            when(historyRepository.countByDocumentId(1L)).thenReturn(3); // Already 3 versions
            when(historyRepository.save(any(ComplianceScoreHistory.class))).thenReturn(testHistory);

            // Act
            complianceHistoryService.archiveScore(testComplianceScore, "OVERRIDE", 2L);

            // Assert
            ArgumentCaptor<ComplianceScoreHistory> captor = ArgumentCaptor.forClass(ComplianceScoreHistory.class);
            verify(historyRepository).save(captor.capture());
            assertEquals(4, captor.getValue().getVersionNumber()); // Should be 4th version
        }

        @Test
        @DisplayName("Should archive score with professor override")
        void archiveScore_WithOverride() {
            // Arrange
            testComplianceScore.setProfessorOverride(95.0);
            testComplianceScore.setProfessorNotes("Excellent work, extra credit awarded");

            when(historyRepository.countByDocumentId(1L)).thenReturn(0);
            when(historyRepository.save(any(ComplianceScoreHistory.class))).thenReturn(testHistory);

            // Act
            complianceHistoryService.archiveScore(testComplianceScore, "OVERRIDE", 2L);

            // Assert
            ArgumentCaptor<ComplianceScoreHistory> captor = ArgumentCaptor.forClass(ComplianceScoreHistory.class);
            verify(historyRepository).save(captor.capture());
            assertEquals(95.0, captor.getValue().getProfessorOverride());
            assertEquals("Excellent work, extra credit awarded", captor.getValue().getProfessorNotes());
        }

        @Test
        @DisplayName("Should not save when current score is null")
        void archiveScore_NullScore() {
            // Act
            complianceHistoryService.archiveScore(null, "RE_EVALUATION", 2L);

            // Assert
            verify(historyRepository, never()).save(any());
            verify(historyRepository, never()).countByDocumentId(anyLong());
        }

        @Test
        @DisplayName("Should archive with OVERRIDE source")
        void archiveScore_OverrideSource() {
            // Arrange
            when(historyRepository.countByDocumentId(1L)).thenReturn(1);
            when(historyRepository.save(any(ComplianceScoreHistory.class))).thenReturn(testHistory);

            // Act
            complianceHistoryService.archiveScore(testComplianceScore, "OVERRIDE", 3L);

            // Assert
            ArgumentCaptor<ComplianceScoreHistory> captor = ArgumentCaptor.forClass(ComplianceScoreHistory.class);
            verify(historyRepository).save(captor.capture());
            assertEquals("OVERRIDE", captor.getValue().getSource());
        }

        @Test
        @DisplayName("Should set recordedAt timestamp")
        void archiveScore_SetsRecordedAt() {
            // Arrange
            when(historyRepository.countByDocumentId(1L)).thenReturn(0);
            when(historyRepository.save(any(ComplianceScoreHistory.class))).thenReturn(testHistory);

            // Act
            complianceHistoryService.archiveScore(testComplianceScore, "RE_EVALUATION", 2L);

            // Assert
            ArgumentCaptor<ComplianceScoreHistory> captor = ArgumentCaptor.forClass(ComplianceScoreHistory.class);
            verify(historyRepository).save(captor.capture());
            assertNotNull(captor.getValue().getRecordedAt());
        }

        @Test
        @DisplayName("Should preserve original evaluatedAt timestamp")
        void archiveScore_PreservesEvaluatedAt() {
            // Arrange
            LocalDateTime originalEvaluatedAt = LocalDateTime.of(2024, 1, 15, 10, 30);
            testComplianceScore.setEvaluatedAt(originalEvaluatedAt);

            when(historyRepository.countByDocumentId(1L)).thenReturn(0);
            when(historyRepository.save(any(ComplianceScoreHistory.class))).thenReturn(testHistory);

            // Act
            complianceHistoryService.archiveScore(testComplianceScore, "RE_EVALUATION", 2L);

            // Assert
            ArgumentCaptor<ComplianceScoreHistory> captor = ArgumentCaptor.forClass(ComplianceScoreHistory.class);
            verify(historyRepository).save(captor.capture());
            assertEquals(originalEvaluatedAt, captor.getValue().getEvaluatedAt());
        }

        @Test
        @DisplayName("Should preserve summary in archive")
        void archiveScore_PreservesSummary() {
            // Arrange
            testComplianceScore.setSummary("Detailed evaluation summary with findings");

            when(historyRepository.countByDocumentId(1L)).thenReturn(0);
            when(historyRepository.save(any(ComplianceScoreHistory.class))).thenReturn(testHistory);

            // Act
            complianceHistoryService.archiveScore(testComplianceScore, "RE_EVALUATION", 2L);

            // Assert
            ArgumentCaptor<ComplianceScoreHistory> captor = ArgumentCaptor.forClass(ComplianceScoreHistory.class);
            verify(historyRepository).save(captor.capture());
            assertEquals("Detailed evaluation summary with findings", captor.getValue().getSummary());
        }
    }

    @Nested
    @DisplayName("Get History for Document Tests")
    class GetHistoryForDocumentTests {

        @Test
        @DisplayName("Should return history for document")
        void getHistoryForDocument_Success() {
            // Arrange
            ComplianceScoreHistory history2 = new ComplianceScoreHistory();
            history2.setId(2L);
            history2.setDocument(testDocument);
            history2.setOverallScore(80.0);
            history2.setVersionNumber(2);
            history2.setRecordedAt(LocalDateTime.now().minusDays(1));

            List<ComplianceScoreHistory> historyList = Arrays.asList(history2, testHistory);
            when(historyRepository.findByDocumentIdOrderByRecordedAtDesc(1L)).thenReturn(historyList);

            // Act
            List<ComplianceScoreHistory> result = complianceHistoryService.getHistoryForDocument(1L);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(historyRepository).findByDocumentIdOrderByRecordedAtDesc(1L);
        }

        @Test
        @DisplayName("Should return empty list when no history exists")
        void getHistoryForDocument_Empty() {
            // Arrange
            when(historyRepository.findByDocumentIdOrderByRecordedAtDesc(99L)).thenReturn(Collections.emptyList());

            // Act
            List<ComplianceScoreHistory> result = complianceHistoryService.getHistoryForDocument(99L);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return single history record")
        void getHistoryForDocument_SingleRecord() {
            // Arrange
            when(historyRepository.findByDocumentIdOrderByRecordedAtDesc(1L))
                    .thenReturn(Collections.singletonList(testHistory));

            // Act
            List<ComplianceScoreHistory> result = complianceHistoryService.getHistoryForDocument(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(testHistory.getId(), result.get(0).getId());
        }

        @Test
        @DisplayName("Should return history ordered by recordedAt descending")
        void getHistoryForDocument_OrderedByDate() {
            // Arrange
            ComplianceScoreHistory older = new ComplianceScoreHistory();
            older.setId(1L);
            older.setRecordedAt(LocalDateTime.now().minusDays(5));

            ComplianceScoreHistory newer = new ComplianceScoreHistory();
            newer.setId(2L);
            newer.setRecordedAt(LocalDateTime.now().minusDays(1));

            // Return in correct order (newest first)
            List<ComplianceScoreHistory> orderedList = Arrays.asList(newer, older);
            when(historyRepository.findByDocumentIdOrderByRecordedAtDesc(1L)).thenReturn(orderedList);

            // Act
            List<ComplianceScoreHistory> result = complianceHistoryService.getHistoryForDocument(1L);

            // Assert
            assertEquals(2, result.size());
            assertEquals(2L, result.get(0).getId()); // Newer first
            assertEquals(1L, result.get(1).getId()); // Older second
        }

        @Test
        @DisplayName("Should handle multiple history records")
        void getHistoryForDocument_MultipleRecords() {
            // Arrange
            ComplianceScoreHistory history1 = new ComplianceScoreHistory();
            history1.setId(1L);
            history1.setVersionNumber(1);

            ComplianceScoreHistory history2 = new ComplianceScoreHistory();
            history2.setId(2L);
            history2.setVersionNumber(2);

            ComplianceScoreHistory history3 = new ComplianceScoreHistory();
            history3.setId(3L);
            history3.setVersionNumber(3);

            List<ComplianceScoreHistory> historyList = Arrays.asList(history3, history2, history1);
            when(historyRepository.findByDocumentIdOrderByRecordedAtDesc(1L)).thenReturn(historyList);

            // Act
            List<ComplianceScoreHistory> result = complianceHistoryService.getHistoryForDocument(1L);

            // Assert
            assertEquals(3, result.size());
        }
    }
}
