package com.reparvalo.controller;

import com.reparvalo.model.DamageExtraction;
import com.reparvalo.model.ReportRequest;
import com.reparvalo.model.TextAnalysisRequest;
import com.reparvalo.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller exposing AI endpoints using Spring AI (Gemini).
 * 
 * Handles free-text parsing requests for damage detection and markdown report 
 * generation requests for trade-in negotiations in Switzerland.
 * 
 * Concurrency is thread-safe as this controller delegates to stateless services.
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    /**
     * Extracts structured damaged parts from a free-text description using NLP.
     * 
     * Accessible via HTTP POST request: POST /api/ai/analyze-text
     * 
     * @param request the payload containing the user's description
     * @return a {@link ResponseEntity} containing the list of identified parts and explanation
     */
    @PostMapping("/analyze-text")
    public ResponseEntity<DamageExtraction> analyzeDamages(@RequestBody TextAnalysisRequest request) {
        if (request.getText() == null || request.getText().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        DamageExtraction result = aiService.extractDamages(request.getText());
        return ResponseEntity.ok(result);
    }

    /**
     * Generates a professional markdown trade-in justification report in the preferred language.
     * 
     * Accessible via HTTP POST request: POST /api/ai/report
     * 
     * @param request the payload containing estimation metrics and language preference
     * @return a {@link ResponseEntity} containing the generated report in markdown
     */
    @PostMapping("/report")
    public ResponseEntity<MapResponse> generateReport(@RequestBody ReportRequest request) {
        if (request.getEstimation() == null) {
            return ResponseEntity.badRequest().build();
        }
        boolean preferFrench = request.getPreferFrench() != null && request.getPreferFrench();
        String report = aiService.generateTradeInReport(request.getEstimation(), preferFrench);
        
        // Wrap string in a JSON response object for clean frontend consumption
        return ResponseEntity.ok(new MapResponse(report));
    }

    /**
     * Inner helper class representing a simple key-value response mapping.
     */
    public static class MapResponse {
        private String content;

        public MapResponse(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}