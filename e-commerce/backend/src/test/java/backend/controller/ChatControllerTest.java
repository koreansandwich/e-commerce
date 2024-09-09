package backend.controller;

import backend.entity.ChatMessage;
import backend.entity.User;
import backend.repository.UserRepository;
import backend.security.JwtUtil;
import backend.service.ChatService;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

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
        // 테스트용 사용자 객체 설정
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setName("Test User");
        mockUser.setPassword("password123");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void testGetChatHistory() throws Exception {
        // Mock 데이터 설정
        ChatMessage message1 = new ChatMessage(mockUser, "Hello", "user", null);
        ChatMessage message2 = new ChatMessage(mockUser, "Hi There!", "bot", null);
        List<ChatMessage> chatMessages = Arrays.asList(message1, message2);

        // 서비스 메서드 Mock 처리
        when(userRepository.findByEmail("test@example.com")).thenReturn(mockUser);
        when(chatService.getChatHistory(anyLong())).thenReturn(chatMessages);

        // GET 요청 테스트
        mockMvc.perform(get("/api/chat/history")
                        .with(csrf()))  // CSRF 토큰을 포함
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(chatMessages)));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void testSendMessage() throws Exception {
        // Mock 데이터 설정
        String userMessage = "Hello, bot!";
        String botResponse = "This is a bot!";
        ChatMessage userChatMessage = new ChatMessage(mockUser, userMessage, "user", null);
        ChatMessage botChatMessage = new ChatMessage(mockUser, botResponse, "bot", null);

        // 첫 번째 요청: 사용자 메시지를 보내고 사용자 메시지를 반환하는 테스트
        when(userRepository.findByEmail("test@example.com")).thenReturn(mockUser);
        when(chatService.saveUserMessage(anyLong(), Mockito.anyString())).thenReturn(userChatMessage);

        mockMvc.perform(post("/api/chat/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userMessage))
                        .with(csrf()))  // CSRF 토큰을 포함
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userChatMessage)));  // 사용자 메시지 반환

        // 두 번째 요청: 봇 응답을 받고 봇 응답을 반환하는 테스트
        when(chatService.saveBotMessage(anyLong(), Mockito.anyString())).thenReturn(botChatMessage);

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userMessage))
                        .with(csrf()))  // CSRF 토큰을 포함
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(botChatMessage)));  // 봇 메시지 반환
    }

}
