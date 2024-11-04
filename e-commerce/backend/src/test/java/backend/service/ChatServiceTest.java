package backend.service;

import backend.entity.ChatMessage;
import backend.entity.User;
import backend.repository.ChatMessageRepository;
import backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @MockBean
    private ChatMessageRepository chatMessageRepository;

    @MockBean
    private UserRepository userRepository;

    private User mockUser;

    @BeforeEach
    public void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setName("Test User");
        mockUser.setPassword("password123");
    }


    @Test
    public void testGetChatHistory() {
        ChatMessage message1 = new ChatMessage(mockUser, "Hello", "user", null);
        ChatMessage message2 = new ChatMessage(mockUser, "Hi There!", "bot", null);
        List<ChatMessage> chatMessages = Arrays.asList(message1, message2);

        when(chatMessageRepository.findByUserIdOrderByTimestampDesc(anyLong())).thenReturn(chatMessages);
        List<ChatMessage> result = chatService.getChatHistory(1L);

        assertEquals(2, result.size());
        verify(chatMessageRepository, times(1)).findByUserIdOrderByTimestampDesc(1L);

    }

    @Test
    void testSaveUserMessage() {
        String userMessage = "Hello bot!";
        ChatMessage chatMessage = new ChatMessage(mockUser, userMessage, "user", null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(chatMessageRepository.save(Mockito.any(ChatMessage.class))).thenReturn(chatMessage);

        ChatMessage result = chatService.saveUserMessage(1L, userMessage);

        assertEquals("Hello bot!", result.getMessage());
        assertEquals("user", result.getSender());
        verify(chatMessageRepository, times(1)).save(Mockito.any(ChatMessage.class));
    }

    @Test
    void testSaveBotMessage() {
        String botMessage = "Hi There!";
        ChatMessage chatMessage = new ChatMessage(mockUser, botMessage, "bot", null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(chatMessageRepository.save(Mockito.any(ChatMessage.class))).thenReturn(chatMessage);

        ChatMessage result = chatService.saveBotMessage(1L, botMessage);

        assertEquals("Hi There!", result.getMessage());
        assertEquals("bot", result.getSender());
        verify(chatMessageRepository, times(1)).save(Mockito.any(ChatMessage.class));
    }

    @Test
    public void testSaveMessageWithUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> chatService.saveUserMessage(1L, "Hello bot!"));
        verify(chatMessageRepository, never()).save(Mockito.any(ChatMessage.class));
    }
}