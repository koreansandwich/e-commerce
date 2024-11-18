package backend.service;

import backend.entity.ChatMessage;
import backend.entity.User;
import backend.repository.ChatMessageRepository;
import backend.repository.UserRepository;
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
    private final FinalMessageService finalMessageService;
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    public ChatService(ChatMessageRepository chatMessageRepository, UserRepository userRepository,
                       KeywordExtractor keywordExtractor, RecommendationService recommendationService,
                       FinalMessageService finalMessageService) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.keywordExtractor = keywordExtractor;
        this.recommendationService = recommendationService;
        this.finalMessageService = finalMessageService;
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
        String botResponse = finalMessageService.generateMessageFromRecommendations(recommendationsJson);

        // 4. 생성된 응답 메시지를 저장
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUser(user);
        chatMessage.setMessage(botResponse);
        chatMessage.setSender("bot");
        chatMessage.setTimestamp(LocalDateTime.now());

        return chatMessageRepository.save(chatMessage);
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
