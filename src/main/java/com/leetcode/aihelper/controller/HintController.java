package com.leetcode.aihelper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leetcode.aihelper.model.HintRequest;
import com.leetcode.aihelper.model.HintResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow Chrome extension to call it
public class HintController {

    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String OPENAI_URL = "https://openrouter.ai/api/v1/chat/completions";

    @PostMapping("/hint")
    public ResponseEntity<HintResponse> getHint(@RequestBody HintRequest request) {
        try {
            // Get the complete prompt string directly from the request body
            String prompt = request.getPrompt();

            // Build OpenAI request payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", "mistralai/mistral-7b-instruct");
            payload.put("messages", List.of(Map.of("role", "user", "content", prompt)));
            payload.put("max_tokens", 300); // Set a reasonable token limit for a full hint

            // Set up headers for the API request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(OPENAI_API_KEY);
            headers.add("HTTP-Referer", "https://leetcode.com");
            headers.add("X-Title", "Leetcode-AI-Helper");

            // Create the HTTP entity with the payload and headers
            HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(payload), headers);
            RestTemplate restTemplate = new RestTemplate();

            // Make the POST request to the OpenAI-compatible API
            ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_URL, entity, Map.class);

            // Extract the AI's reply from the response
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, String> message = (Map<String, String>) firstChoice.get("message");
            String content = message.get("content");

            // Return the hint in a formatted response
            return ResponseEntity.ok(new HintResponse(content.trim()));

        } catch (Exception e) {
            // Log the error and return an appropriate error response
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new HintResponse("ERROR: " + e.getMessage()));
        }
    }
}