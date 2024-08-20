package backend.controller;

import backend.entity.ChatMessage;
import backend.repository.UserRepository;
import backend.entity.User;
import backend.service.ChatService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    public ChatController(ChatService chatService, UserRepository userRepository) {
        this.chatService = chatService;
        this.userRepository = userRepository;
    }

    @GetMapping("/history")
    public List<ChatMessage> getChatHistory(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        return chatService.getChatHistory(user.getId());
    }

    @PostMapping("/send")
    public ChatMessage sendMessage(@RequestBody String message, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);

        ChatMessage userMessage = chatService.saveUserMessage(user.getId(), message);
        String botResponse = generateBotResponse(message);
        chatService.saveBotMessage(user.getId(), botResponse);

        return userMessage;
    }

    private User getUserFromAuthentication(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }

    private String generateBotResponse(String message) {
        return "This is a bot response to your message: " + message;
    }
}
