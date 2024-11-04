package backend.service;


import backend.entity.ChatMessage;
import backend.entity.User;
import backend.repository.ChatMessageRepository;
import backend.repository.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final String OPENAI_API_KEY;


    public ChatService(ChatMessageRepository chatMessageRepository, UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        Dotenv dotenv = Dotenv.load();
        this.OPENAI_API_KEY = dotenv.get("OPENAI_API_KEY");
        System.out.println("OPENAI_API_KEY: " + OPENAI_API_KEY);
    }

    public List<ChatMessage> getChatHistory(Long userId) {
        return chatMessageRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public ChatMessage saveUserMessage(Long userId, String message) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUser(user);
        chatMessage.setMessage(message);
        chatMessage.setSender("user");
        chatMessage.setTimestamp(LocalDateTime.now());

        //Step1. 키워드 추출 및 추천 시스템 호출

        Map<String, Integer> keywordScores = KeywordExtractor.extractKeywordsWithScore(message);
        System.out.println(keywordScores);

        // 추천 시스템과의 통신에 대한 로직


        return chatMessageRepository.save(chatMessage);
    }

    public ChatMessage saveBotMessage(Long userId, String userMessage) {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        String url = "https://api.openai.com/v1/chat/completions";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(OPENAI_API_KEY);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", new JSONArray().put(new JSONObject().put("role", "user").put("content", userMessage)));
        requestBody.put("max_tokens", 150);
        requestBody.put("temperature", 0.7);

        System.out.println("Using API Key: " + OPENAI_API_KEY);
        headers.setBearerAuth(OPENAI_API_KEY);
        System.out.println("Authorization Header: Bearer " + OPENAI_API_KEY);

        System.out.println("Request Body: " + requestBody.toString());

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        int maxAttempts = 5; // 최대 재시도 횟수
        int attempt = 0;
        long waitTime = 1000; // 1초

        while (attempt < maxAttempts) {
            try {
                System.out.println("Sending request to OpenAI API... Attempt " + (attempt + 1));

                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    System.out.println("OpenAI API Response: " + response.getBody());

                    JSONObject responseBody = new JSONObject(response.getBody());
                    String botResponse = responseBody.getJSONArray("choices").getJSONObject(0)
                            .getJSONObject("message").getString("content").trim();

                    System.out.println("Extracted Bot Response: " + botResponse);

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setUser(user);
                    chatMessage.setMessage(botResponse);
                    chatMessage.setSender("bot");
                    chatMessage.setTimestamp(LocalDateTime.now());

                    return chatMessageRepository.save(chatMessage);

                } else {
                    System.out.println("Failed to request from OpenAI. Status Code: " + response.getStatusCode());
                    throw new RuntimeException("Failed to request from OpenAI");
                }

            } catch (Exception e) {
                attempt++;
                if (attempt >= maxAttempts) {
                    System.out.println("Max retry attempts reached. Failed to request from OpenAI API.");
                    throw new RuntimeException("Failed to request from OpenAI after " + maxAttempts + " attempts.", e);
                } else {
                    System.out.println("Request failed. Retrying in " + waitTime + " ms...");
                    try {
                        Thread.sleep(waitTime); // 대기 시간
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Thread interrupted during backoff sleep.", ie);
                    }
                    waitTime *= 2; // 대기 시간을 2배로 증가 (지수적 증가)
                }
            }
        }
        throw new RuntimeException("Unexpected error during API call.");
    }


    private String recommendProducts(String keywords) {
        // 실제 추천 시스템 구현
        return "example recommend products";
    }
}