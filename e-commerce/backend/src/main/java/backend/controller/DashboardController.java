package backend.controller;

import backend.DTO.ItemDTO;
import backend.entity.Item;
import backend.entity.UserRecommendation;
import backend.entity.User;
import backend.repository.ItemRepository;
import backend.security.JwtUtil;
import backend.service.UserRecommendationService;
import backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final UserService userService;
    private final UserRecommendationService userRecommendationService;
    private final JwtUtil jwtUtil;
    private final ItemRepository itemRepository;

    // 생성자 주입
    public DashboardController(UserService userService, UserRecommendationService userRecommendationService, JwtUtil jwtUtil, ItemRepository itemRepository) {
        this.userService = userService;
        this.userRecommendationService = userRecommendationService;
        this.jwtUtil = jwtUtil;
        this.itemRepository = itemRepository;
    }

    /**
     * 사용자의 연령, 성별, 선택적 카테고리에 따라 추천 항목을 반환합니다.
     *
     * @param itemType 추천받을 카테고리 (선택적)
     * @param token    JWT 토큰 (Authorization 헤더)
     * @return 추천 항목 리스트
     */
    @GetMapping("/recommendations_user")
    public ResponseEntity<List<ItemDTO>> getRecommendations(
            @RequestParam(required = false) String itemType,
            @RequestHeader("Authorization") String token
    ) {
        // JWT 토큰에서 사용자 이메일 추출
        String jwt = token.substring(7); // "Bearer " 제거
        String email = jwtUtil.extractUsername(jwt); // JWT에서 이메일 추출

        // 이메일을 통해 사용자 정보 조회
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 사용자 연령대 계산
        String ageGroup = calculateAgeGroup(user.getAge());
        System.out.println("[DEBUG] Calculated Age Group: " + ageGroup);

        // 추천 항목 조회
        List<UserRecommendation> recommendations;
        if (itemType == null || itemType.isEmpty()) {
            System.out.println("[DEBUG] Fetching recommendations for all categories.");
            recommendations = userRecommendationService.getDistinctItemTypes(user.getGender(), ageGroup);
        } else {
            System.out.println("[DEBUG] Fetching recommendations for specific category: " + itemType);
            recommendations = userRecommendationService.getRecommendations(user.getGender(), ageGroup, itemType);
        }

        List<ItemDTO> recommendationDTOs = recommendations.stream().map(rec -> {
            Item item = itemRepository.findById(rec.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found for ID: " + rec.getItemId()));
            ItemDTO dto = new ItemDTO(
                    item.getItemId(),
                    item.getItemName(),
                    item.getItemImageUrl(),
                    item.getItemLink(),
                    item.getItemFinalPrice(),
                    item.getBrand()
            );
            System.out.println("[DEBUG] Created ItemDTO: " + dto);
            return dto;
        }).toList();
        System.out.println("[DEBUG] Final DTO List: " + recommendationDTOs);


        return ResponseEntity.ok(recommendationDTOs);
    }

    /**
     * 사용자의 나이에 따른 연령 그룹을 계산합니다.
     *
     * @param age 사용자 나이
     * @return 연령 그룹 (예: "10", "20", "30", ...)
     */
    private String calculateAgeGroup(int age) {
        if (age >= 10 && age <= 19) {
            return "10";
        } else if (age >= 20 && age <= 29) {
            return "20";
        } else if (age >= 30 && age <= 39) {
            return "30";
        } else if (age >= 40 && age <= 49) {
            return "40";
        } else if (age >= 50 && age <= 59) {
            return "50";
        } else {
            return "60";
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String token) {
        // JWT 토큰에서 사용자 이메일 추출
        String jwt = token.substring(7); // "Bearer " 제거
        String email = jwtUtil.extractUsername(jwt); // JWT에서 이메일 추출

        // 이메일을 통해 사용자 정보 조회
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        System.out.println("[DEBUG] Retrieved User: " + user);

        return ResponseEntity.ok(user);
    }

}
