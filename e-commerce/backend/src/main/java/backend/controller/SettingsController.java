package backend.controller;

import backend.DTO.MessageDTO;
import backend.entity.ChatMessage;
import backend.service.SettingsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;


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

    // 사용자 계정 정보 업데이트
    @PutMapping("/account/{userId}")
    public String updateUserAccount(@PathVariable Long userId,
                                    @RequestParam(required = false) String newName,
                                    @RequestParam(required = false) String newPassword,
                                    @RequestParam(required = false) String confirmPassword,
                                    @RequestParam(required = false) Integer newAge,
                                    @RequestParam(required = false) String newGender) {
        settingsService.updateUserAccount(userId, newName, newPassword, confirmPassword, newAge, newGender);
        return "User account updated successfully";
    }
}
