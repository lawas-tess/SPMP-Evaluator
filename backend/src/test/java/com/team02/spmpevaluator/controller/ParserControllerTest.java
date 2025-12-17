package com.team02.spmpevaluator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team02.spmpevaluator.dto.ParserConfigurationDTO;
import com.team02.spmpevaluator.entity.ParserConfiguration;
import com.team02.spmpevaluator.entity.ParserFeedback;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.SPMPDocument;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.SPMPDocumentRepository;
import com.team02.spmpevaluator.service.ParserConfigurationService;
import com.team02.spmpevaluator.service.ParserFeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for ParserController.
 * Tests parser configuration and feedback management endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ParserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ParserConfigurationService parserConfigurationService;

    @MockBean
    private ParserFeedbackService parserFeedbackService;

    @MockBean
    private SPMPDocumentRepository documentRepository;

    private User professor;
    private User student;
    private ParserConfiguration parserConfig;
    private ParserFeedback parserFeedback;
    private SPMPDocument document;

    @BeforeEach
    void setUp() {
        // Create test professor
        professor = new User();
        professor.setId(1L);
        professor.setUsername("professor1");
        professor.setEmail("professor1@example.com");
        professor.setFirstName("Jane");
        professor.setLastName("Professor");
        professor.setRole(Role.PROFESSOR);
        professor.setEnabled(true);

        // Create test student
        student = new User();
        student.setId(2L);
        student.setUsername("student1");
        student.setEmail("student1@example.com");
        student.setFirstName("John");
        student.setLastName("Student");
        student.setRole(Role.STUDENT);
        student.setEnabled(true);

        // Create test parser configuration
        parserConfig = new ParserConfiguration();
        parserConfig.setId(1L);
        parserConfig.setName("IEEE 1058 Standard");
        parserConfig.setDescription("Default configuration for IEEE 1058 compliance");
        parserConfig.setClauseMappings("[{\"clauseId\": \"1.1\", \"clauseName\": \"Purpose\"}]");
        parserConfig.setCustomRules("[{\"ruleId\": \"R1\", \"description\": \"Check completeness\"}]");
        parserConfig.setIsActive(true);
        parserConfig.setIsDefault(true);
        parserConfig.setCreatedBy(professor);
        parserConfig.setCreatedAt(LocalDateTime.now());
        parserConfig.setUpdatedAt(LocalDateTime.now());

        // Create test document
        document = new SPMPDocument();
        document.setId(1L);
        document.setFileName("test-spmp.pdf");
        document.setUploadedBy(student);

        // Create test parser feedback
        parserFeedback = new ParserFeedback();
        parserFeedback.setId(1L);
        parserFeedback.setDocument(document);
        parserFeedback.setParserConfiguration(parserConfig);
        parserFeedback.setComplianceScore(85.0);
        parserFeedback.setDetectedClauses("[{\"clauseId\": \"1.1\", \"found\": true}]");
        parserFeedback.setMissingClauses("[{\"clauseId\": \"2.3\", \"severity\": \"high\"}]");
        parserFeedback.setRecommendations("[{\"priority\": \"high\", \"recommendation\": \"Add risk management\"}]");
        parserFeedback.setAnalysisReport("Document analysis completed");
        parserFeedback.setParserVersion("1.0");
        parserFeedback.setStatus(ParserFeedback.FeedbackStatus.COMPLETED);
        parserFeedback.setAnalyzedAt(LocalDateTime.now());
    }

    // ============= Parser Configuration Tests =============

    @Nested
    @DisplayName("POST /api/parser/config")
    class CreateConfiguration {

        // Note: Full success test requires proper CustomUserDetails mock (integration
        // test)
        // These tests focus on role-based access control which @WithMockUser handles
        // correctly

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("PROFESSOR should be authorized to access create configuration endpoint")
        void createConfiguration_ProfessorAuthorized() throws Exception {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            dto.setName("New Config");
            dto.setDescription("New parser configuration");
            dto.setClauseMappings("[{\"clauseId\": \"1.1\"}]");
            dto.setCustomRules("[{\"ruleId\": \"R1\"}]");
            dto.setIsActive(true);
            dto.setIsDefault(false);

            // @WithMockUser doesn't set up CustomUserDetails so endpoint returns 400
            // due to null userDetails, but importantly it's NOT 403 (role is authorized)
            mockMvc.perform(post("/api/parser/config")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void createConfiguration_ForbiddenForStudent() throws Exception {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            dto.setName("New Config");

            mockMvc.perform(post("/api/parser/config")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isForbidden());

            verify(parserConfigurationService, never()).createConfiguration(any(), any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 403 for ADMIN role")
        void createConfiguration_ForbiddenForAdmin() throws Exception {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            dto.setName("New Config");

            mockMvc.perform(post("/api/parser/config")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void createConfiguration_Unauthenticated() throws Exception {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            dto.setName("New Config");

            mockMvc.perform(post("/api/parser/config")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("PUT /api/parser/config/{id}")
    class UpdateConfiguration {

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should update parser configuration successfully")
        void updateConfiguration_Success() throws Exception {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            dto.setName("Updated Config");
            dto.setDescription("Updated description");
            dto.setIsActive(true);
            dto.setIsDefault(false);

            when(parserConfigurationService.updateConfiguration(eq(1L), any(ParserConfiguration.class)))
                    .thenReturn(parserConfig);

            mockMvc.perform(put("/api/parser/config/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));

            verify(parserConfigurationService).updateConfiguration(eq(1L), any(ParserConfiguration.class));
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 400 when update fails")
        void updateConfiguration_Failure() throws Exception {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            dto.setName("Updated Config");

            when(parserConfigurationService.updateConfiguration(eq(1L), any(ParserConfiguration.class)))
                    .thenThrow(new RuntimeException("Configuration not found"));

            mockMvc.perform(put("/api/parser/config/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Failed to update parser configuration")));
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void updateConfiguration_ForbiddenForStudent() throws Exception {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            dto.setName("Updated Config");

            mockMvc.perform(put("/api/parser/config/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isForbidden());

            verify(parserConfigurationService, never()).updateConfiguration(anyLong(), any());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void updateConfiguration_Unauthenticated() throws Exception {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            dto.setName("Updated Config");

            mockMvc.perform(put("/api/parser/config/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("GET /api/parser/config")
    class GetActiveConfigurations {

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return all active configurations")
        void getActiveConfigurations_Success() throws Exception {
            List<ParserConfiguration> configs = Arrays.asList(parserConfig);
            when(parserConfigurationService.getActiveConfigurations()).thenReturn(configs);

            mockMvc.perform(get("/api/parser/config"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("IEEE 1058 Standard"));

            verify(parserConfigurationService).getActiveConfigurations();
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return empty list when no configurations exist")
        void getActiveConfigurations_EmptyList() throws Exception {
            when(parserConfigurationService.getActiveConfigurations()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/parser/config"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void getActiveConfigurations_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/parser/config"))
                    .andExpect(status().isForbidden());

            verify(parserConfigurationService, never()).getActiveConfigurations();
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void getActiveConfigurations_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/parser/config"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("GET /api/parser/config/{id}")
    class GetConfigurationById {

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return configuration by ID")
        void getConfigurationById_Success() throws Exception {
            when(parserConfigurationService.getConfigurationById(1L)).thenReturn(Optional.of(parserConfig));

            mockMvc.perform(get("/api/parser/config/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("IEEE 1058 Standard"));

            verify(parserConfigurationService).getConfigurationById(1L);
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 404 when configuration not found")
        void getConfigurationById_NotFound() throws Exception {
            when(parserConfigurationService.getConfigurationById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/parser/config/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void getConfigurationById_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/parser/config/1"))
                    .andExpect(status().isForbidden());

            verify(parserConfigurationService, never()).getConfigurationById(anyLong());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void getConfigurationById_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/parser/config/1"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("GET /api/parser/config/default")
    class GetDefaultConfiguration {

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return default configuration")
        void getDefaultConfiguration_Success() throws Exception {
            when(parserConfigurationService.getDefaultConfiguration()).thenReturn(Optional.of(parserConfig));

            mockMvc.perform(get("/api/parser/config/default"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.isDefault").value(true));

            verify(parserConfigurationService).getDefaultConfiguration();
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 404 when no default configuration exists")
        void getDefaultConfiguration_NotFound() throws Exception {
            when(parserConfigurationService.getDefaultConfiguration()).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/parser/config/default"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void getDefaultConfiguration_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/parser/config/default"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void getDefaultConfiguration_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/parser/config/default"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("PUT /api/parser/config/{id}/set-default")
    class SetAsDefault {

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should set configuration as default successfully")
        void setAsDefault_Success() throws Exception {
            when(parserConfigurationService.setAsDefault(1L)).thenReturn(parserConfig);

            mockMvc.perform(put("/api/parser/config/1/set-default")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.isDefault").value(true));

            verify(parserConfigurationService).setAsDefault(1L);
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 400 when setting default fails")
        void setAsDefault_Failure() throws Exception {
            when(parserConfigurationService.setAsDefault(999L))
                    .thenThrow(new RuntimeException("Configuration not found"));

            mockMvc.perform(put("/api/parser/config/999/set-default")
                    .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Failed to set as default")));
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void setAsDefault_ForbiddenForStudent() throws Exception {
            mockMvc.perform(put("/api/parser/config/1/set-default")
                    .with(csrf()))
                    .andExpect(status().isForbidden());

            verify(parserConfigurationService, never()).setAsDefault(anyLong());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void setAsDefault_Unauthenticated() throws Exception {
            mockMvc.perform(put("/api/parser/config/1/set-default")
                    .with(csrf()))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("DELETE /api/parser/config/{id}")
    class DeleteConfiguration {

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should delete configuration successfully")
        void deleteConfiguration_Success() throws Exception {
            doNothing().when(parserConfigurationService).deleteConfiguration(1L);

            mockMvc.perform(delete("/api/parser/config/1")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Parser configuration deleted successfully"));

            verify(parserConfigurationService).deleteConfiguration(1L);
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 400 when deletion fails")
        void deleteConfiguration_Failure() throws Exception {
            doThrow(new RuntimeException("Cannot delete default configuration"))
                    .when(parserConfigurationService).deleteConfiguration(1L);

            mockMvc.perform(delete("/api/parser/config/1")
                    .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Failed to delete configuration")));
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void deleteConfiguration_ForbiddenForStudent() throws Exception {
            mockMvc.perform(delete("/api/parser/config/1")
                    .with(csrf()))
                    .andExpect(status().isForbidden());

            verify(parserConfigurationService, never()).deleteConfiguration(anyLong());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void deleteConfiguration_Unauthenticated() throws Exception {
            mockMvc.perform(delete("/api/parser/config/1")
                    .with(csrf()))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("POST /api/parser/config/create-default")
    class CreateDefaultConfiguration {

        // Note: Full success test requires proper CustomUserDetails mock (integration
        // test)
        // These tests focus on role-based access control which @WithMockUser handles
        // correctly

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("PROFESSOR should be authorized to access create-default endpoint")
        void createDefaultConfiguration_ProfessorAuthorized() throws Exception {
            // @WithMockUser doesn't set up CustomUserDetails so endpoint returns 400
            // due to null userDetails, but importantly it's NOT 403 (role is authorized)
            mockMvc.perform(post("/api/parser/config/create-default")
                    .with(csrf()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void createDefaultConfiguration_ForbiddenForStudent() throws Exception {
            mockMvc.perform(post("/api/parser/config/create-default")
                    .with(csrf()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void createDefaultConfiguration_Unauthenticated() throws Exception {
            mockMvc.perform(post("/api/parser/config/create-default")
                    .with(csrf()))
                    .andExpect(status().is3xxRedirection());
        }
    }

    // ============= Parser Feedback Tests =============

    @Nested
    @DisplayName("POST /api/parser/feedback/{documentId}/generate-mock")
    class GenerateMockFeedback {

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should generate mock feedback successfully")
        void generateMockFeedback_Success() throws Exception {
            when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
            when(parserConfigurationService.getDefaultConfiguration()).thenReturn(Optional.of(parserConfig));
            when(parserFeedbackService.generateMockFeedback(any(SPMPDocument.class), any(ParserConfiguration.class)))
                    .thenReturn(parserFeedback);

            mockMvc.perform(post("/api/parser/feedback/1/generate-mock")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.complianceScore").value(85.0))
                    .andExpect(jsonPath("$.status").value("COMPLETED"));

            verify(parserFeedbackService).generateMockFeedback(any(SPMPDocument.class), any(ParserConfiguration.class));
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 400 when document not found")
        void generateMockFeedback_DocumentNotFound() throws Exception {
            when(documentRepository.findById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/parser/feedback/999/generate-mock")
                    .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Failed to generate feedback")));
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 400 when no default configuration exists")
        void generateMockFeedback_NoDefaultConfig() throws Exception {
            when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
            when(parserConfigurationService.getDefaultConfiguration()).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/parser/feedback/1/generate-mock")
                    .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Failed to generate feedback")));
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void generateMockFeedback_ForbiddenForStudent() throws Exception {
            mockMvc.perform(post("/api/parser/feedback/1/generate-mock")
                    .with(csrf()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void generateMockFeedback_Unauthenticated() throws Exception {
            mockMvc.perform(post("/api/parser/feedback/1/generate-mock")
                    .with(csrf()))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("GET /api/parser/feedback/document/{documentId}")
    class GetFeedbackByDocument {

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("PROFESSOR should access feedback by document")
        void getFeedbackByDocument_Professor_Success() throws Exception {
            List<ParserFeedback> feedbacks = Arrays.asList(parserFeedback);
            when(parserFeedbackService.getFeedbackByDocumentId(1L)).thenReturn(feedbacks);

            mockMvc.perform(get("/api/parser/feedback/document/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].complianceScore").value(85.0));

            verify(parserFeedbackService).getFeedbackByDocumentId(1L);
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("STUDENT should access feedback by document")
        void getFeedbackByDocument_Student_Success() throws Exception {
            List<ParserFeedback> feedbacks = Arrays.asList(parserFeedback);
            when(parserFeedbackService.getFeedbackByDocumentId(1L)).thenReturn(feedbacks);

            mockMvc.perform(get("/api/parser/feedback/document/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));

            verify(parserFeedbackService).getFeedbackByDocumentId(1L);
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return empty list when no feedback exists")
        void getFeedbackByDocument_EmptyList() throws Exception {
            when(parserFeedbackService.getFeedbackByDocumentId(1L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/parser/feedback/document/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 403 for ADMIN role")
        void getFeedbackByDocument_ForbiddenForAdmin() throws Exception {
            mockMvc.perform(get("/api/parser/feedback/document/1"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void getFeedbackByDocument_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/parser/feedback/document/1"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("GET /api/parser/feedback/{id}")
    class GetFeedbackById {

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("PROFESSOR should access feedback by ID")
        void getFeedbackById_Professor_Success() throws Exception {
            when(parserFeedbackService.getFeedbackById(1L)).thenReturn(Optional.of(parserFeedback));

            mockMvc.perform(get("/api/parser/feedback/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.complianceScore").value(85.0));

            verify(parserFeedbackService).getFeedbackById(1L);
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("STUDENT should access feedback by ID")
        void getFeedbackById_Student_Success() throws Exception {
            when(parserFeedbackService.getFeedbackById(1L)).thenReturn(Optional.of(parserFeedback));

            mockMvc.perform(get("/api/parser/feedback/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));

            verify(parserFeedbackService).getFeedbackById(1L);
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 404 when feedback not found")
        void getFeedbackById_NotFound() throws Exception {
            when(parserFeedbackService.getFeedbackById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/parser/feedback/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 403 for ADMIN role")
        void getFeedbackById_ForbiddenForAdmin() throws Exception {
            mockMvc.perform(get("/api/parser/feedback/1"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void getFeedbackById_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/parser/feedback/1"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("GET /api/parser/feedback/my-documents")
    class GetMyDocumentsFeedback {

        // Note: Full success test requires proper CustomUserDetails mock (integration
        // test)
        // This endpoint uses @AuthenticationPrincipal CustomUserDetails which
        // @WithMockUser doesn't provide
        // These tests focus on role-based access control

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 403 for PROFESSOR role")
        void getMyDocumentsFeedback_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(get("/api/parser/feedback/my-documents"))
                    .andExpect(status().isForbidden());

            verify(parserFeedbackService, never()).getFeedbackByUserId(anyLong());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 403 for ADMIN role")
        void getMyDocumentsFeedback_ForbiddenForAdmin() throws Exception {
            mockMvc.perform(get("/api/parser/feedback/my-documents"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void getMyDocumentsFeedback_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/parser/feedback/my-documents"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("Role-Based Access Control Tests")
    class RoleBasedAccessTests {

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("PROFESSOR should have access to configuration endpoints")
        void professorHasConfigAccess() throws Exception {
            when(parserConfigurationService.getActiveConfigurations()).thenReturn(Collections.emptyList());
            when(parserConfigurationService.getDefaultConfiguration()).thenReturn(Optional.of(parserConfig));
            when(parserConfigurationService.getConfigurationById(1L)).thenReturn(Optional.of(parserConfig));

            mockMvc.perform(get("/api/parser/config"))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/api/parser/config/default"))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/api/parser/config/1"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("STUDENT should be denied access to configuration endpoints")
        void studentDeniedConfigAccess() throws Exception {
            mockMvc.perform(get("/api/parser/config"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/parser/config/default"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/parser/config/1"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(post("/api/parser/config")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("STUDENT should have access to feedback endpoints (except my-documents which requires CustomUserDetails)")
        void studentHasFeedbackAccess() throws Exception {
            when(parserFeedbackService.getFeedbackByDocumentId(1L)).thenReturn(Collections.emptyList());
            when(parserFeedbackService.getFeedbackById(1L)).thenReturn(Optional.of(parserFeedback));

            // These endpoints work without CustomUserDetails
            mockMvc.perform(get("/api/parser/feedback/document/1"))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/api/parser/feedback/1"))
                    .andExpect(status().isOk());

            // Note: /api/parser/feedback/my-documents requires CustomUserDetails
            // which @WithMockUser doesn't provide, tested separately for role access only
        }
    }
}
