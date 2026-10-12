
package com.debugdairy.backend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final GroqService groqService;
    private final AnalysisRepository analysisRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnalysisController(
            GroqService groqService,
            AnalysisRepository analysisRepository) {

        this.groqService = groqService;
        this.analysisRepository = analysisRepository;
    }

    // 1. Analyse code using Groq and save the result
    @PostMapping
    public Map<String, String> analyzeCode(
            @RequestBody Map<String, String> request) {

        String code = request.get("code");
        String language = request.get("language");

        if (code == null || code.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Code cannot be empty");
        }

        if (language == null || language.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Programming language is required");
        }

        try {
            String analysis = groqService.analyzeCode(code, language);

            Map<String, String> result = objectMapper.readValue(
                    analysis,
                    new TypeReference<Map<String, String>>() {}
            );

            Long id = analysisRepository.saveAnalysis(
                    language, code, result);

            result.put("id", String.valueOf(id));

            return result;

        } catch (Exception e) {
            System.err.println(
                    "Analysis processing error: " + e.getMessage());

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Unable to process or save the code analysis.");
        }
    }

    // 2. Retrieve all saved analyses
    @GetMapping
    public List<Map<String, Object>> getAllAnalyses() {
        return analysisRepository.findAll();
    }

    // 3. Retrieve one analysis by ID
    @GetMapping("/{id}")
    public Map<String, Object> getAnalysisById(
            @PathVariable Long id) {

        Map<String, Object> analysis =
                analysisRepository.findById(id);

        if (analysis == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Analysis not found");
        }

        return analysis;
    }

    // 4. Delete an analysis by ID
    @DeleteMapping("/{id}")
    public Map<String, String> deleteAnalysis(
            @PathVariable Long id) {

        boolean deleted = analysisRepository.deleteById(id);

        if (!deleted) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Analysis not found");
        }

        return Map.of(
                "message", "Analysis deleted successfully",
                "id", String.valueOf(id)
        );
    }

    // 5. Retrieve learning history
    @GetMapping("/learning")
    public List<Map<String, Object>> getLearningHistory() {
        return analysisRepository.findAll();
    }
}
