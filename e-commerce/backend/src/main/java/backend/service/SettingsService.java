package backend.service;

import backend.DTO.MessageDTO;
import backend.entity.ChatMessage;
import backend.entity.User;
import backend.repository.ChatMessageRepository;
import backend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SettingsService {

    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserAccountByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Autowired
    public SettingsService(UserRepository userRepository, ChatMessageRepository chatMessageRepository) {
        this.userRepository = userRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void updateUserAccount(Long userId, String newName, String newBirthDate, String newGender) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (newName != null && !newName.isEmpty()) {
            user.setName(newName);
        }

        if (newBirthDate != null && !newBirthDate.isEmpty()) {
            LocalDate birthDate = LocalDate.parse(newBirthDate);
            user.setBirthDate(birthDate);
        }

        if (newGender != null && !newGender.isEmpty()) {
            user.setGender(newGender);
        }

        userRepository.save(user);
    }

    // 사용자 챗봇 대화 내역 불러오기
    public List<ChatMessage> getUserChatHistory(Long userId) {
        return chatMessageRepository.findByUserIdOrderByTimestampAsc(userId);
    }

    // 사용자 챗봇 대화 내역 삭제
    public void deleteUserChatHistory(Long userId) {
        List<ChatMessage> chatMessages = chatMessageRepository.findByUserId(userId);
        chatMessageRepository.deleteAll(chatMessages);
    }

}
