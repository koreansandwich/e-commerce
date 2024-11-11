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

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SettingsService {

    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SettingsService(UserRepository userRepository, ChatMessageRepository chatMessageRepository) {
        this.userRepository = userRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // 사용자 챗봇 대화 내역 불러오기
    public List<ChatMessage> getUserChatHistory(Long userId) {
        return chatMessageRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    // 사용자 챗봇 대화 내역 삭제
    public void deleteUserChatHistory(Long userId) {
        List<ChatMessage> chatMessages = chatMessageRepository.findByUserId(userId);
        chatMessageRepository.deleteAll(chatMessages);
    }

    // 사용자 계정 정보 업데이트
    public void updateUserAccount(Long userId, String newName, String newPassword, String confirmPassword, Integer newAge, String newGender) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User existingUser = user.get();

            // 이름 업데이트
            if (newName != null && !newName.isEmpty()) {
                existingUser.setName(newName);
            }

            // 비밀번호 업데이트 (비밀번호 확인 일치 여부 확인 및 암호화)
            if (newPassword != null && !newPassword.isEmpty()) {
                if (newPassword.equals(confirmPassword)) {
                    existingUser.setPassword(passwordEncoder.encode(newPassword));
                } else {
                    throw new IllegalArgumentException("Passwords do not match");
                }
            }

            // 나이 업데이트
            if (newAge != null) {
                existingUser.setAge(newAge);
            }

            // 성별 업데이트
            if (newGender != null && !newGender.isEmpty()) {
                existingUser.setGender(newGender);
            }

            userRepository.save(existingUser);
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
