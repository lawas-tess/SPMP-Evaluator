package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.entity.ParserConfiguration;
import com.team02.spmpevaluator.entity.ParserFeedback;
import com.team02.spmpevaluator.entity.SPMPDocument;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.ParserFeedbackRepository;
import com.team02.spmpevaluator.util.DocumentParser;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ParserFeedbackService.
 * Tests AI-based document analysis and IEEE 1058 compliance feedback.
 */
@ExtendWith(MockitoExtension.class)
class ParserFeedbackServiceTest {

    @Mock
    private ParserFeedbackRepository parserFeedbackRepository;

    @Mock
    private OpenRouterService openRouterService;

    @Mock
    private DocumentParser documentParser;

    @InjectMocks
    private ParserFeedbackService parserFeedbackService;

    private User testUser;
    private SPMPDocument testDocument;
    private ParserConfiguration testConfig;
    private ParserFeedback testFeedback;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("student@test.com");
        testUser.setFirstName("Test");
        testUser.setLastName("Student");

        // Setup test document
        testDocument = new SPMPDocument();
        testDocument.setId(1L);
        testDocument.setFileName("test_spmp.pdf");
        testDocument.setFileUrl("/uploads/test_spmp.pdf");
        testDocument.setUploadedBy(testUser);

        // Setup test configuration
        testConfig = new ParserConfiguration();
        testConfig.setId(1L);
        testConfig.setName("Test Configuration");

        // Setup test feedback
        testFeedback = new ParserFeedback();
        testFeedback.setId(1L);
        testFeedback.setDocument(testDocument);
        testFeedback.setParserConfiguration(testConfig);
        testFeedback.setComplianceScore(75.0);
        testFeedback.setStatus(ParserFeedback.FeedbackStatus.COMPLETED);
        testFeedback.setAnalyzedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Create Feedback Tests")
    class CreateFeedbackTests {

        @Test
        @DisplayName("Should create feedback with pending status")
        void createFeedback_Success() {
            // Arrange
            when(parserFeedbackRepository.save(any(ParserFeedback.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            ParserFeedback result = parserFeedbackService.createFeedback(testDocument, testConfig);

            // Assert
            assertNotNull(result);
            assertEquals(ParserFeedback.FeedbackStatus.PENDING, result.getStatus());
            assertEquals(testDocument, result.getDocument());
            assertEquals(testConfig, result.getParserConfiguration());
        }

        @Test
        @DisplayName("Should set analyzed timestamp on creation")
        void createFeedback_SetsTimestamp() {
            // Arrange
            when(parserFeedbackRepository.save(any(ParserFeedback.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            ParserFeedback result = parserFeedbackService.createFeedback(testDocument, testConfig);

            // Assert
            assertNotNull(result.getAnalyzedAt());
        }

        @Test
        @DisplayName("Should set parser version")
        void createFeedback_SetsParserVersion() {
            // Arrange
            when(parserFeedbackRepository.save(any(ParserFeedback.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            ParserFeedback result = parserFeedbackService.createFeedback(testDocument, testConfig);

            // Assert
            assertEquals("1.0.0-MOCK", result.getParserVersion());
        }
    }

    @Nested
    @DisplayName("Analyze Document with AI Tests")
    class AnalyzeDocumentWithAITests {

        @Test
        @DisplayName("Should analyze document successfully with AI")
        void analyzeDocumentWithAI_Success() throws Exception {
            // Arrange
            String documentContent = "Test SPMP document content with overview and references.";
            Map<String, Object> aiAnalysis = Map.of(
                    "complianceScore", 85.0,
                    "detectedClauses", Arrays.asList(Map.of("clauseId", "1", "clauseName", "Overview")),
                    "missingClauses", Arrays.asList(Map.of("clauseId", "3", "clauseName", "Definitions")),
                    "recommendations", Arrays.asList(Map.of("priority", "high", "recommendation", "Add definitions")),
                    "summary", "Document analysis complete");

            when(documentParser.extractTextFromFile(testDocument.getFileUrl())).thenReturn(documentContent);
            when(openRouterService.analyzeDocument(documentContent)).thenReturn(aiAnalysis);
            when(openRouterService.isConfigured()).thenReturn(true);
            when(parserFeedbackRepository.save(any(ParserFeedback.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            ParserFeedback result = parserFeedbackService.analyzeDocumentWithAI(testDocument, testConfig);

            // Assert
            assertNotNull(result);
            assertEquals(85.0, result.getComplianceScore());
            assertEquals(ParserFeedback.FeedbackStatus.COMPLETED, result.getStatus());
            assertEquals("1.0.0-AI", result.getParserVersion());
        }

        @Test
        @DisplayName("Should fail when document content is empty")
        void analyzeDocumentWithAI_EmptyContent() throws Exception {
            // Arrange
            when(documentParser.extractTextFromFile(testDocument.getFileUrl())).thenReturn("");
            when(parserFeedbackRepository.save(any(ParserFeedback.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            ParserFeedback result = parserFeedbackService.analyzeDocumentWithAI(testDocument, testConfig);

            // Assert
            assertEquals(ParserFeedback.FeedbackStatus.FAILED, result.getStatus());
            assertTrue(result.getErrorMessage().contains("Could not extract text"));
        }

        @Test
        @DisplayName("Should fail when document content is null")
        void analyzeDocumentWithAI_NullContent() throws Exception {
            // Arrange
            when(documentParser.extractTextFromFile(testDocument.getFileUrl())).thenReturn(null);
            when(parserFeedbackRepository.save(any(ParserFeedback.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            ParserFeedback result = parserFeedbackService.analyzeDocumentWithAI(testDocument, testConfig);

            // Assert
            assertEquals(ParserFeedback.FeedbackStatus.FAILED, result.getStatus());
        }

        @Test
        @DisplayName("Should fallback to mock data on AI failure")
        void analyzeDocumentWithAI_FallbackOnError() throws Exception {
            // Arrange
            when(documentParser.extractTextFromFile(testDocument.getFileUrl())).thenReturn("Some content");
            when(openRouterService.analyzeDocument(any())).thenThrow(new RuntimeException("AI service unavailable"));
            when(parserFeedbackRepository.save(any(ParserFeedback.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            ParserFeedback result = parserFeedbackService.analyzeDocumentWithAI(testDocument, testConfig);

            // Assert
            assertEquals(ParserFeedback.FeedbackStatus.COMPLETED, result.getStatus());
            assertEquals("1.0.0-FALLBACK", result.getParserVersion());
            assertEquals(50.0, result.getComplianceScore());
        }

        @Test
        @DisplayName("Should use mock version when AI not configured")
        void analyzeDocumentWithAI_MockVersion() throws Exception {
            // Arrange
            when(documentParser.extractTextFromFile(testDocument.getFileUrl())).thenReturn("Content");
            when(openRouterService.analyzeDocument(any())).thenReturn(Map.of("complianceScore", 70.0));
            when(openRouterService.isConfigured()).thenReturn(false);
            when(parserFeedbackRepository.save(any(ParserFeedback.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            ParserFeedback result = parserFeedbackService.analyzeDocumentWithAI(testDocument, testConfig);

            // Assert
            assertEquals("1.0.0-MOCK", result.getParserVersion());
        }
    }

    @Nested
    @DisplayName("Get Feedback Tests")
    class GetFeedbackTests {

        @Test
        @DisplayName("Should get feedback by document")
        void getFeedbackByDocument_Success() {
            // Arrange
            when(parserFeedbackRepository.findByDocument(testDocument)).thenReturn(Optional.of(testFeedback));

            // Act
            Optional<ParserFeedback> result = parserFeedbackService.getFeedbackByDocument(testDocument);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(testFeedback.getId(), result.get().getId());
        }

        @Test
        @DisplayName("Should return empty when no feedback exists for document")
        void getFeedbackByDocument_NotFound() {
            // Arrange
            when(parserFeedbackRepository.findByDocument(testDocument)).thenReturn(Optional.empty());

            // Act
            Optional<ParserFeedback> result = parserFeedbackService.getFeedbackByDocument(testDocument);

            // Assert
            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Should get all feedback by document ID")
        void getFeedbackByDocumentId_Success() {
            // Arrange
            ParserFeedback feedback2 = new ParserFeedback();
            feedback2.setId(2L);
            when(parserFeedbackRepository.findByDocumentId(1L))
                    .thenReturn(Arrays.asList(testFeedback, feedback2));

            // Act
            List<ParserFeedback> result = parserFeedbackService.getFeedbackByDocumentId(1L);

            // Assert
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should get feedback by ID")
        void getFeedbackById_Success() {
            // Arrange
            when(parserFeedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));

            // Act
            Optional<ParserFeedback> result = parserFeedbackService.getFeedbackById(1L);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(75.0, result.get().getComplianceScore());
        }

        @Test
        @DisplayName("Should get feedback by user ID")
        void getFeedbackByUserId_Success() {
            // Arrange
            when(parserFeedbackRepository.findByDocumentUploadedByIdOrderByAnalyzedAtDesc(1L))
                    .thenReturn(Arrays.asList(testFeedback));

            // Act
            List<ParserFeedback> result = parserFeedbackService.getFeedbackByUserId(1L);

            // Assert
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should return empty list when user has no feedback")
        void getFeedbackByUserId_Empty() {
            // Arrange
            when(parserFeedbackRepository.findByDocumentUploadedByIdOrderByAnalyzedAtDesc(99L))
                    .thenReturn(Collections.emptyList());

            // Act
            List<ParserFeedback> result = parserFeedbackService.getFeedbackByUserId(99L);

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Update Status Tests")
    class UpdateStatusTests {

        @Test
        @DisplayName("Should update feedback status")
        void updateStatus_Success() {
            // Arrange
            testFeedback.setStatus(ParserFeedback.FeedbackStatus.PENDING);
            when(parserFeedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));
            when(parserFeedbackRepository.save(any(ParserFeedback.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            ParserFeedback result = parserFeedbackService.updateStatus(1L, ParserFeedback.FeedbackStatus.COMPLETED,
                    null);

            // Assert
            assertEquals(ParserFeedback.FeedbackStatus.COMPLETED, result.getStatus());
        }

        @Test
        @DisplayName("Should update status with error message")
        void updateStatus_WithErrorMessage() {
            // Arrange
            when(parserFeedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));
            when(parserFeedbackRepository.save(any(ParserFeedback.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            ParserFeedback result = parserFeedbackService.updateStatus(1L, ParserFeedback.FeedbackStatus.FAILED,
                    "Parse error");

            // Assert
            assertEquals(ParserFeedback.FeedbackStatus.FAILED, result.getStatus());
            assertEquals("Parse error", result.getErrorMessage());
        }

        @Test
        @DisplayName("Should throw exception when feedback not found")
        void updateStatus_NotFound() {
            // Arrange
            when(parserFeedbackRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> parserFeedbackService.updateStatus(99L, ParserFeedback.FeedbackStatus.COMPLETED, null));
            assertTrue(exception.getMessage().contains("not found"));
        }

        @Test
        @DisplayName("Should not set error message when null")
        void updateStatus_NullErrorMessage() {
            // Arrange
            testFeedback.setErrorMessage("Previous error");
            when(parserFeedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));
            when(parserFeedbackRepository.save(any(ParserFeedback.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            ParserFeedback result = parserFeedbackService.updateStatus(1L, ParserFeedback.FeedbackStatus.COMPLETED,
                    null);

            // Assert
            assertEquals("Previous error", result.getErrorMessage()); // Should remain unchanged
        }
    }

    @Nested
    @DisplayName("Delete Feedback Tests")
    class DeleteFeedbackTests {

        @Test
        @DisplayName("Should delete feedback successfully")
        void deleteFeedback_Success() {
            // Act
            parserFeedbackService.deleteFeedback(1L);

            // Assert
            verify(parserFeedbackRepository).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("Generate Mock Feedback Tests")
    class GenerateMockFeedbackTests {

        @Test
        @DisplayName("Should generate mock feedback by calling AI analysis")
        void generateMockFeedback_CallsAIAnalysis() throws Exception {
            // Arrange
            when(documentParser.extractTextFromFile(any())).thenReturn("Document content");
            when(openRouterService.analyzeDocument(any())).thenReturn(Map.of("complianceScore", 80.0));
            when(openRouterService.isConfigured()).thenReturn(false);
            when(parserFeedbackRepository.save(any(ParserFeedback.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            ParserFeedback result = parserFeedbackService.generateMockFeedback(testDocument, testConfig);

            // Assert
            assertNotNull(result);
            verify(documentParser).extractTextFromFile(testDocument.getFileUrl());
        }
    }

    @Nested
    @DisplayName("Feedback Status Enum Tests")
    class FeedbackStatusTests {

        @Test
        @DisplayName("Should support PENDING status")
        void feedbackStatus_Pending() {
            // Arrange
            testFeedback.setStatus(ParserFeedback.FeedbackStatus.PENDING);

            // Assert
            assertEquals(ParserFeedback.FeedbackStatus.PENDING, testFeedback.getStatus());
        }

        @Test
        @DisplayName("Should support COMPLETED status")
        void feedbackStatus_Completed() {
            // Arrange
            testFeedback.setStatus(ParserFeedback.FeedbackStatus.COMPLETED);

            // Assert
            assertEquals(ParserFeedback.FeedbackStatus.COMPLETED, testFeedback.getStatus());
        }

        @Test
        @DisplayName("Should support FAILED status")
        void feedbackStatus_Failed() {
            // Arrange
            testFeedback.setStatus(ParserFeedback.FeedbackStatus.FAILED);

            // Assert
            assertEquals(ParserFeedback.FeedbackStatus.FAILED, testFeedback.getStatus());
        }
    }
}
