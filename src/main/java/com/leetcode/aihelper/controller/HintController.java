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

    private static final String OPENAI_API_KEY = "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"; // Your new OpenRouter key
 
    private static final String OPENAI_URL = "https://openrouter.ai/api/v1/chat/completions";



    @PostMapping("/hint")
    public ResponseEntity<HintResponse> getHint(@RequestBody HintRequest request) {
        String prompt = "Give a helpful, beginner-friendly hint for solving the LeetCode problem titled: " + request.getProblemTitle();

        try {
            // Build OpenAI request
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", "mistralai/mistral-7b-instruct");
            payload.put("messages", List.of(Map.of("role", "user", "content", prompt)));
            payload.put("max_tokens", 100);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(OPENAI_API_KEY);
            headers.add("HTTP-Referer", "https://leetcode.com"); // You can replace with your website if needed
            headers.add("X-Title", "Leetcode-AI-Helper");

            HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(payload), headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_URL, entity, Map.class);

            // Extract the AI's reply
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, String> message = (Map<String, String>) firstChoice.get("message");
            String content = message.get("content");

            return ResponseEntity.ok(new HintResponse(content.trim()));

        } catch (Exception e) {
            e.printStackTrace(); // still prints in terminal
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new HintResponse("ERROR: " + e.getMessage())); // shows error in response
        }

    }
}
