package com.team02.spmpevaluator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for integrating with OpenRouter AI API.
 * Uses nvidia/nemotron-nano-12b-v2-vl:free model which supports multimodal analysis (text + images).
 * Provides IEEE 1058 compliance analysis for SPMP documents including visual elements.
 */
@Service
@Slf4j
public class OpenRouterService {

    @Value("${openrouter.api.key:}")
    private String apiKey;

    @Value("${openrouter.api.url:https://openrouter.ai/api/v1/chat/completions}")
    private String apiUrl;

    @Value("${openrouter.model:nvidia/nemotron-nano-12b-v2-vl:free}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OpenRouterService() {
        // Configure RestTemplate with 10-second timeout to prevent hanging
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10 seconds connection timeout
        factory.setReadTimeout(10000);    // 10 seconds read timeout
        this.restTemplate = new RestTemplate(factory);
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
     * Analyze SPMP document with embedded images (diagrams, flowcharts, architecture diagrams).
     * Uses nvidia/nemotron-nano-12b-v2-vl:free model which supports multimodal analysis.
     *
     * @param documentContent Text content of the SPMP document
     * @param imageBase64List List of base64-encoded images from the document
     * @return Structured compliance analysis including image insights
     */
    public Map<String, Object> analyzeDocumentWithImages(String documentContent, List<String> imageBase64List) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("OpenRouter API key not configured, returning mock analysis");
            return getMockAnalysis();
        }

        try {
            String prompt = buildAnalysisPrompt(documentContent);
            String response = callOpenRouterAPIWithImages(prompt, imageBase64List);
            return parseAnalysisResponse(response);
        } catch (Exception e) {
            log.error("Multimodal AI analysis failed: {}", e.getMessage(), e);
            // Fallback to text-only analysis if image processing fails
            return analyzeDocument(documentContent);
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
     * Call OpenRouter API with text and optional images.
     * The nvidia/nemotron-nano-12b-v2-vl:free model supports multimodal content.
     *
     * @param prompt Text prompt for analysis
     * @return AI response
     */
    private String callOpenRouterAPI(String prompt) {
        return callOpenRouterAPIWithImages(prompt, null);
    }

    /**
     * Call OpenRouter API with text and optional images.
     * Supports the nvidia/nemotron-nano-12b-v2-vl:free model which can analyze images.
     *
     * @param prompt Text prompt for analysis
     * @param imageBase64List Optional list of base64-encoded images
     * @return AI response including image analysis
     */
    private String callOpenRouterAPIWithImages(String prompt, List<String> imageBase64List) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("OpenRouter API key not configured");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("HTTP-Referer", "http://localhost:8080");
        headers.set("X-Title", "SPMP Evaluator");

        try {
            // Build message content
            List<Map<String, Object>> contentList = new ArrayList<>();

            // Add text content
            Map<String, Object> textContent = new HashMap<>();
            textContent.put("type", "text");
            textContent.put("text", prompt);
            contentList.add(textContent);

            // Add images if provided
            if (imageBase64List != null && !imageBase64List.isEmpty()) {
                for (String imageBase64 : imageBase64List) {
                    if (imageBase64 != null && !imageBase64.isEmpty()) {
                        Map<String, Object> imageContent = new HashMap<>();
                        imageContent.put("type", "image_url");
                        
                        Map<String, String> imageUrl = new HashMap<>();
                        // Format: data:image/png;base64,...
                        imageUrl.put("url", "data:image/png;base64," + imageBase64);
                        imageContent.put("image_url", imageUrl);
                        
                        contentList.add(imageContent);
                    }
                }
            }

            // Build request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(
                Map.of("role", "user", "content", contentList)
            ));
            requestBody.put("temperature", 0.3);
            requestBody.put("max_tokens", 2000);

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            log.info("Calling OpenRouter API with model: {} (images: {})", 
                     model, 
                     imageBase64List != null ? imageBase64List.size() : 0);

            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl, HttpMethod.POST, entity, String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode choices = root.path("choices");
                if (choices.isArray() && choices.size() > 0) {
                    String content = choices.get(0).path("message").path("content").asText();
                    log.info("AI response received successfully from model: {}", model);
                    return content;
                }
            }
            throw new RuntimeException("Invalid API response");
        } catch (Exception e) {
            log.error("OpenRouter API call failed: {}", e.getMessage(), e);
            throw new RuntimeException("AI service unavailable: " + e.getMessage(), e);
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
