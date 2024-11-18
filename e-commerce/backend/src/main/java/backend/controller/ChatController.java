package backend.controller;

import backend.DTO.MessageDTO;
import backend.entity.ChatMessage;
import backend.repository.UserRepository;
import backend.entity.User;
import backend.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    public ChatController(ChatService chatService, UserRepository userRepository) {
        this.chatService = chatService;
        this.userRepository = userRepository;
    }

    @GetMapping("/history")
    public List<MessageDTO> getChatHistory(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        List<ChatMessage> messages = chatService.getChatHistory(user.getId());

        // ChatMessage -> MessageDTO로 변환
        List<MessageDTO> messageDTOs = new ArrayList<>();
        for (ChatMessage message : messages) {
            messageDTOs.add(new MessageDTO(
                    message.getMessage(),
                    message.getSender(),
                    message.getUser().getEmail(), // 사용자 정보
                    message.getTimestamp()
            ));
        }

        if (messages.isEmpty()) {
            logger.info("No chat messages found for user: {}", user.getEmail());
        } else {
            logger.info("Chat history retrieved: {}", messages);
        }

        return messageDTOs;
    }

    @PostMapping("/send")
    public ChatMessage sendUserMessage(@RequestBody MessageDTO messageDTO, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);

        String message = messageDTO.getText();
        ChatMessage userMessage = chatService.saveUserMessage(user.getId(), message);
        return userMessage;
    }

    @PostMapping("/bot-response")
    public ChatMessage getBotResponse(@RequestBody MessageDTO messageDTO, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);

        String message = messageDTO.getText();
        ChatMessage botMessage = chatService.saveBotMessage(user.getId(), message);
        return botMessage;
    }

    private User getUserFromAuthentication(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }
}