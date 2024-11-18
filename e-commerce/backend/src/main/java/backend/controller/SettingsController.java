package backend.controller;

import backend.DTO.MessageDTO;
import backend.entity.ChatMessage;
import backend.entity.User;
import backend.security.JwtUtil;
import backend.service.SettingsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;
    private JwtUtil jwtUtil;


    // 사용자 챗봇 대화 내역 가져오기
    @GetMapping("/chat-history/{userId}")
    public List<ChatMessage> getUserChatHistory(@PathVariable Long userId) {
        return settingsService.getUserChatHistory(userId);
    }

    // 사용자 챗봇 대화 내역 삭제
    @DeleteMapping("/chat-history/{userId}")
    public String deleteUserChatHistory(@PathVariable Long userId) {
        settingsService.deleteUserChatHistory(userId);
        return "User chat history deleted successfully";
    }

    @GetMapping("/account")
    public User getAccountInformation(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // "Bearer " 제거
        String email = jwtUtil.extractUsername(jwt); // JWT에서 이메일 추출
        return settingsService.getUserAccountByEmail(email); // 이메일로 사용자 정보 반환
    }


    @GetMapping("/account/{userId}")
    public User getUserAccount(@PathVariable Long userId) {
        return settingsService.getUserById(userId);
    }

    @PutMapping("/account/{userId}")
    public String updateUserAccount(
            @PathVariable Long userId,
            @RequestBody Map<String, String> updateData) {
        String newName = updateData.get("name");
        String newBirthDate = updateData.get("birthDate");
        String newGender = updateData.get("gender");

        settingsService.updateUserAccount(userId, newName, newBirthDate, newGender);
        return "User account updated successfully";
    }


}
