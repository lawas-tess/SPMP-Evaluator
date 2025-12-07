package com.team02.spmpevaluator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Service for integrating with OpenRouter AI API.
 * Provides IEEE 1058 compliance analysis using AI models.
 */
@Service
@Slf4j
public class OpenRouterService {

    @Value("${openrouter.api.key:}")
    private String apiKey;

    @Value("${openrouter.api.url:https://openrouter.ai/api/v1/chat/completions}")
    private String apiUrl;

    @Value("${openrouter.model:amazon/nova-lite-v1:free}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OpenRouterService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Analyze SPMP document content for IEEE 1058 compliance.
     * Returns structured feedback including detected clauses, missing clauses, and recommendations.
     */
    public Map<String, Object> analyzeDocument(String documentContent) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("OpenRouter API key not configured, returning mock analysis");
            return getMockAnalysis();
        }

        try {
            String prompt = buildAnalysisPrompt(documentContent);
            String response = callOpenRouterAPI(prompt);
            return parseAnalysisResponse(response);
        } catch (Exception e) {
            log.error("AI analysis failed: {}", e.getMessage(), e);
            return getMockAnalysis();
        }
    }

    /**
     * Generate detailed feedback for a specific section.
     */
    public String generateSectionFeedback(String sectionName, String sectionContent) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "Section '" + sectionName + "' requires review. Please ensure it follows IEEE 1058 guidelines.";
        }

        try {
            String prompt = buildSectionPrompt(sectionName, sectionContent);
            return callOpenRouterAPI(prompt);
        } catch (Exception e) {
            log.error("Section feedback generation failed: {}", e.getMessage());
            return "Unable to generate feedback for section '" + sectionName + "'.";
        }
    }

    /**
     * Build the analysis prompt for IEEE 1058 compliance checking.
     */
    private String buildAnalysisPrompt(String documentContent) {
        // Truncate content if too long (API limits)
        String truncatedContent = documentContent.length() > 8000 
            ? documentContent.substring(0, 8000) + "...[truncated]" 
            : documentContent;

        return """
            You are an expert in IEEE 1058 Software Project Management Plan (SPMP) standards.
            Analyze the following SPMP document and provide a compliance assessment.
            
            Respond ONLY with a valid JSON object (no markdown, no extra text) in this exact format:
            {
                "complianceScore": <number 0-100>,
                "detectedClauses": [
                    {"clauseId": "1", "clauseName": "Overview", "score": <0-100>, "found": true, "location": "description"},
                    ...
                ],
                "missingClauses": [
                    {"clauseId": "3", "clauseName": "Definitions", "severity": "high|medium|low", "reason": "explanation"},
                    ...
                ],
                "recommendations": [
                    {"priority": "high|medium|low", "recommendation": "specific advice", "clauseRef": "clause number"},
                    ...
                ],
                "summary": "Brief overall assessment"
            }
            
            IEEE 1058 Required Clauses to check:
            1. Overview (Project summary, purpose, scope)
            2. References (Related documents, standards)
            3. Definitions (Glossary, acronyms)
            4. Project Organization (Structure, roles)
            5. Managerial Process Plans (Start-up, Work, Control, Risk plans)
            6. Technical Process Plans (Development methodology)
            7. Supporting Process Plans (Configuration, QA, Documentation)
            
            DOCUMENT CONTENT:
            %s
            """.formatted(truncatedContent);
    }

    /**
     * Build prompt for section-specific feedback.
     */
    private String buildSectionPrompt(String sectionName, String sectionContent) {
        return """
            Analyze this SPMP section for IEEE 1058 compliance and provide specific improvement recommendations.
            
            Section: %s
            Content: %s
            
            Provide 2-3 specific, actionable recommendations to improve this section.
            """.formatted(sectionName, sectionContent);
    }

    /**
     * Call OpenRouter API with the given prompt.
     */
    private String callOpenRouterAPI(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("HTTP-Referer", "http://localhost:8080");
        headers.set("X-Title", "SPMP Evaluator");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(
            Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("temperature", 0.3);
        requestBody.put("max_tokens", 2000);

        try {
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            log.info("Calling OpenRouter API with model: {}", model);
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl, HttpMethod.POST, entity, String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode choices = root.path("choices");
                if (choices.isArray() && choices.size() > 0) {
                    String content = choices.get(0).path("message").path("content").asText();
                    log.info("AI response received successfully");
                    return content;
                }
            }
            throw new RuntimeException("Invalid API response");
        } catch (Exception e) {
            log.error("OpenRouter API call failed: {}", e.getMessage());
            throw new RuntimeException("AI service unavailable: " + e.getMessage());
        }
    }

    /**
     * Parse the AI response into structured data.
     */
    private Map<String, Object> parseAnalysisResponse(String response) {
        try {
            // Clean up response - remove markdown code blocks if present
            String cleanResponse = response.trim();
            if (cleanResponse.startsWith("```json")) {
                cleanResponse = cleanResponse.substring(7);
            }
            if (cleanResponse.startsWith("```")) {
                cleanResponse = cleanResponse.substring(3);
            }
            if (cleanResponse.endsWith("```")) {
                cleanResponse = cleanResponse.substring(0, cleanResponse.length() - 3);
            }
            cleanResponse = cleanResponse.trim();

            return objectMapper.readValue(cleanResponse, Map.class);
        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", e.getMessage());
            log.debug("Raw response: {}", response);
            
            // Return partial result with raw summary
            Map<String, Object> result = getMockAnalysis();
            result.put("summary", "AI Analysis: " + response.substring(0, Math.min(500, response.length())));
            return result;
        }
    }

    /**
     * Return mock analysis when AI is unavailable.
     */
    private Map<String, Object> getMockAnalysis() {
        Map<String, Object> result = new HashMap<>();
        result.put("complianceScore", 65.0);
        result.put("detectedClauses", List.of(
            Map.of("clauseId", "1", "clauseName", "Overview", "score", 80, "found", true, "location", "Beginning of document"),
            Map.of("clauseId", "2", "clauseName", "References", "score", 60, "found", true, "location", "References section"),
            Map.of("clauseId", "4", "clauseName", "Project Organization", "score", 70, "found", true, "location", "Organization section")
        ));
        result.put("missingClauses", List.of(
            Map.of("clauseId", "3", "clauseName", "Definitions", "severity", "medium", "reason", "No glossary or definitions section found"),
            Map.of("clauseId", "6", "clauseName", "Technical Process Plans", "severity", "high", "reason", "Missing development methodology"),
            Map.of("clauseId", "7", "clauseName", "Supporting Process Plans", "severity", "high", "reason", "No QA or configuration management plans")
        ));
        result.put("recommendations", List.of(
            Map.of("priority", "high", "recommendation", "Add Technical Process Plans section describing development methodology", "clauseRef", "6"),
            Map.of("priority", "high", "recommendation", "Include Supporting Process Plans for QA and configuration management", "clauseRef", "7"),
            Map.of("priority", "medium", "recommendation", "Add a Definitions/Glossary section for technical terms", "clauseRef", "3")
        ));
        result.put("summary", "Document shows partial IEEE 1058 compliance. Key sections present but missing critical technical and supporting process plans.");
        return result;
    }

    /**
     * Check if AI service is properly configured.
     */
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }
}
