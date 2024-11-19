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
    private final SettingsService settingsService;
    private final JwtUtil jwtUtil;

    public SettingsController(SettingsService settingsService, JwtUtil jwtUtil) {
        this.settingsService = settingsService;
        this.jwtUtil = jwtUtil;
    }

    // 사용자 챗봇 대화 내역 가져오기
    @GetMapping("/chat-history")
    public List<ChatMessage> getUserChatHistory(@RequestHeader("Authorization") String token) {
        // JWT 토큰에서 사용자 이메일 추출
        String jwt = token.substring(7); // "Bearer " 제거
        String email = jwtUtil.extractUsername(jwt); // JWT에서 이메일 추출

        // 이메일로 사용자 정보 가져오기
        User user = settingsService.getUserAccountByEmail(email);

        // 사용자 ID로 챗봇 대화 내역 반환
        return settingsService.getUserChatHistory(user.getId());
    }


    @DeleteMapping("/chat-history")
    public String deleteUserChatHistory(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // "Bearer " 제거
        String email = jwtUtil.extractUsername(jwt); // JWT에서 이메일 추출
        User user = settingsService.getUserAccountByEmail(email); // 사용자 검색
        settingsService.deleteUserChatHistory(user.getId()); // 사용자 ID로 대화 삭제
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

    @PutMapping("/account/password")
    public String changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> passwordData) {
        String jwt = token.substring(7); // "Bearer " 제거
        String email = jwtUtil.extractUsername(jwt); // JWT에서 이메일 추출
        String currentPassword = passwordData.get("currentPassword");
        String newPassword = passwordData.get("newPassword");

        settingsService.changePassword(email, currentPassword, newPassword);
        return "Password updated successfully";
    }



}
