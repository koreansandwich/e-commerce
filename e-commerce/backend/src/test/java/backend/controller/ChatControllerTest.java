package backend.controller;

import backend.entity.ChatMessage;
import backend.entity.User;
import backend.repository.UserRepository;
import backend.security.JwtUtil;
import backend.service.ChatService;
import backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;// 이 줄을 추가하세요.



@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithMockUser(username = "test@example.com")
    public void testGetChatHistory() throws Exception {
        ChatMessage message1 = new ChatMessage(mockUser, "Hello", "user", null);
        ChatMessage message2 = new ChatMessage(mockUser, "Hi There!", "bot", null);
        List<ChatMessage> chatMessages = Arrays.asList(message1, message2);

        when(userRepository.findByEmail("test@example.com")).thenReturn(mockUser);
        when(chatService.getChatHistory(anyLong())).thenReturn(chatMessages);

        mockMvc.perform(get("/api/chat/history"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(chatMessages)));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void testSendMessage() throws Exception {
        String userMessage = "Hello, bot!";
        String botResponse = "This is a bot!";

        ChatMessage chatMessage = new ChatMessage(mockUser, userMessage, "user", null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(mockUser);
        when(chatService.saveUserMessage(anyLong(), Mockito.anyString())).thenReturn(chatMessage);
        when(chatService.saveBotMessage(anyLong(), Mockito.anyString())).thenReturn(new ChatMessage(mockUser, botResponse, "bot", null));

        mockMvc.perform(post("/api/chat.send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userMessage)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(chatMessage)));
    }
}