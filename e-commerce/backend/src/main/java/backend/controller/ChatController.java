package backend.controller;

import backend.entity.ChatMessage;
import backend.repository.UserRepository;
import backend.entity.User;
import backend.service.ChatService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public ChatMessage sendUserMessage(@RequestBody String message, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);

        ChatMessage userMessage = chatService.saveUserMessage(user.getId(), message);
        return userMessage;
    }

    @PostMapping
    public ChatMessage getBotResponse(@RequestBody String message, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);

        ChatMessage botMessage = chatService.saveBotMessage(user.getId(), message);
        return botMessage;
    }

    private User getUserFromAuthentication(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }
}