
package com.debugdairy.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.groq.com/openai/v1")
            .build();

    public String analyzeCode(String code, String language) {

        Map<String, Object> requestBody = Map.of(
            "model", "openai/gpt-oss-20b",
            "messages", List.of(
                Map.of(
                    "role", "system",
                    "content", """
                        You are DebugDairy, a beginner-friendly programming
                        analysis assistant.

                        Analyze the submitted code and return ONLY valid JSON
                        with these exact fields:
                        {
                          "issueTitle": "Short title of the likely mistake",
                          "explanation": "Clear explanation of the issue",
                          "correctedCode": "Suggested corrected code",
                          "category": "Syntax, Logic, Input Handling, or Other",
                          "conceptToRevise": "Concept the student should revise"
                        }

                        If no obvious mistake exists, say so in issueTitle
                        and explain that further testing may be needed.
                        Use escaped newline characters where needed in JSON
                        string values.
                        Do not claim that code was executed or compiled.
                        Do not include Markdown fences around the JSON.
                        """
                ),
                Map.of(
                    "role", "user",
                    "content", "Programming language: " + language
                            + "\n\nCode:\n" + code
                )
            )
        );

        Map<?, ?> response = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new IllegalStateException(
                    "Groq returned an empty response.");
        }

        List<?> choices = (List<?>) response.get("choices");

        if (choices == null || choices.isEmpty()) {
            throw new IllegalStateException(
                    "Groq returned no analysis choices.");
        }

        Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
        Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");

        if (message == null || message.get("content") == null) {
            throw new IllegalStateException(
                    "Groq returned no analysis content.");
        }

        return message.get("content").toString();
    }
}
