package backend.service;

import backend.entity.ChatMessage;
import backend.entity.User;
import backend.repository.ChatMessageRepository;
import backend.repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final KeywordExtractor keywordExtractor;
    private final RecommendationService recommendationService;
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    public ChatService(ChatMessageRepository chatMessageRepository, UserRepository userRepository,
                       KeywordExtractor keywordExtractor, RecommendationService recommendationService,
                       FinalMessageService finalMessageService) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.keywordExtractor = keywordExtractor;
        this.recommendationService = recommendationService;
    }

    /**
     * 사용자 메시지를 저장.
     */
    public ChatMessage saveUserMessage(Long userId, String message) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUser(user);
        chatMessage.setMessage(message);
        chatMessage.setSender("user");
        chatMessage.setTimestamp(LocalDateTime.now());

        return chatMessageRepository.save(chatMessage);
    }

    /**
     * GPT와 연동해 사용자 요청을 처리하고 응답 메시지를 저장.
     */
    public ChatMessage saveBotMessage(Long userId, String userInput) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // 1. 사용자 입력 메시지에서 키워드 및 추천 유형 추출
        Map<String, Object> analysisResult = keywordExtractor.analyzeMessageWithGPT(userInput);

        // 2. 추천 시스템에 데이터 요청
        String recommendationType = (String) analysisResult.get("recommendationType");
        String recommendationsJson = recommendationService.getRecommendations(recommendationType, analysisResult);

        // 3. 추천 데이터를 기반으로 자연스러운 메시지 생성
        String botResponse = generateStructuredResponse(recommendationsJson);

        // 4. 생성된 응답 메시지를 저장
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUser(user);
        chatMessage.setMessage(botResponse);
        chatMessage.setSender("bot");
        chatMessage.setTimestamp(LocalDateTime.now());

        return chatMessageRepository.save(chatMessage);
    }

    private String generateStructuredResponse(String recommendationsJson) {
        try {
            // JSON 데이터를 파싱
            JSONArray recommendations = new JSONArray(recommendationsJson);
            StringBuilder responseBuilder = new StringBuilder();

            // 추천된 제품 정보 구성
            responseBuilder.append("안녕하세요! 다음은 추천된 제품입니다:\n\n");
            for (int i = 0; i < Math.min(1, recommendations.length()); i++) { // 최대 1개 제품만 표시
                JSONObject product = recommendations.getJSONObject(i);
                responseBuilder.append("제품명: ").append(product.optString("item_name", "제품명 없음")).append("\n")
                        .append("가격: ").append(product.optString("item_final_price", "가격 정보 없음")).append("원\n")
                        .append("브랜드: ").append(product.optString("brand", "브랜드 정보 없음")).append("\n")
                        .append("링크: ").append(product.optString("item_link", "링크 없음")).append("\n\n"); // 링크 추가
            }

            return responseBuilder.toString(); // 최종 메시지 반환
        } catch (Exception e) {
            logger.error("Failed to generate structured response", e);
            return "추천 결과를 생성하는 중 오류가 발생했습니다.";
        }
    }

    public List<ChatMessage> getChatHistory(Long userId) {
        List<ChatMessage> chatHistory = chatMessageRepository.findByUserIdOrderByTimestampAsc(userId);

        // 콘솔 출력
        if (chatHistory.isEmpty()) {
            logger.info("No chat history found for userId: {}", userId);
        } else {
            logger.info("Chat history for userId {}: {}", userId, chatHistory);
        }

        return chatHistory;
    }
}
