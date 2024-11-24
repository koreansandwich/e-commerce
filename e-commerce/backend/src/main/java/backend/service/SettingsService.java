package backend.service;

import backend.DTO.MessageDTO;
import backend.entity.ChatMessage;
import backend.entity.User;
import backend.entity.UserHistory;
import backend.repository.ChatMessageRepository;
import backend.repository.UserHistoryRepository;
import backend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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
    private final UserHistoryRepository userHistoryRepository;

    public User getUserAccountByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Autowired
    public SettingsService(UserRepository userRepository, ChatMessageRepository chatMessageRepository, UserHistoryRepository userHistoryRepository) {
        this.userRepository = userRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userHistoryRepository = userHistoryRepository;
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

    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // 새 비밀번호 설정
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 사용자 모든 기록 삭제
    @Transactional
    public void resetUserHistory(Long userId) {
        // ChatMessage 삭제
        chatMessageRepository.deleteAll(chatMessageRepository.findByUserId(userId));
        // UserHistory 삭제
        userHistoryRepository.deleteAllByUserId(userId);
    }


}
