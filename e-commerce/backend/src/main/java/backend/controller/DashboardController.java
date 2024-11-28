package backend.controller;

import backend.DTO.ItemDTO;
import backend.DTO.UserStatisticsDTO;
import backend.entity.Item;
import backend.entity.UserRecommendation;
import backend.entity.User;
import backend.repository.ItemRepository;
import backend.security.JwtUtil;
import backend.service.UserRecommendationService;
import backend.service.UserService;
import backend.service.UserStatisticsService;
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
    private final UserStatisticsService userStatisticsService;

    // 생성자 주입
    public DashboardController(UserService userService, UserRecommendationService userRecommendationService, JwtUtil jwtUtil, ItemRepository itemRepository, UserStatisticsService userStatisticsService) {
        this.userService = userService;
        this.userRecommendationService = userRecommendationService;
        this.jwtUtil = jwtUtil;
        this.itemRepository = itemRepository;
        this.userStatisticsService = userStatisticsService;
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

    @GetMapping("/statistics")
    public ResponseEntity<UserStatisticsDTO> getUserStatistics(@RequestHeader("Authorization") String token) {
        // JWT에서 사용자 이메일 추출
        String jwt = token.substring(7); // "Bearer " 제거
        String email = jwtUtil.extractUsername(jwt); // JWT에서 이메일 추출

        // 이메일로 사용자 조회
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 통계 데이터 생성
        UserStatisticsDTO statistics = userStatisticsService.getUserStatistics(user.getId());
        if (statistics == null) {
            System.out.println("[WARN] No statistics found for userId: " + user.getId());
        } else {
            System.out.println("[DEBUG] Retrieved Statistics: " + statistics);
        }
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/rating-distribution")
    public ResponseEntity<Map<Integer, Integer>> getRatingDistribution(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // "Bearer " 제거
        String email = jwtUtil.extractUsername(jwt); // JWT에서 이메일 추출

        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        Map<Integer, Integer> ratingDistribution = userStatisticsService.getRatingDistribution(user.getId());
        return ResponseEntity.ok(ratingDistribution);
    }

    @GetMapping("/top-keywords")
    public ResponseEntity<List<String>> getTopKeywords(@RequestHeader("Authorization") String token) {
        // JWT에서 사용자 이메일 추출
        String jwt = token.substring(7); // "Bearer " 제거
        String email = jwtUtil.extractUsername(jwt); // JWT에서 이메일 추출

        // 이메일로 사용자 조회
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 상위 키워드 가져오기
        List<String> topKeywords = userStatisticsService.getTopKeywords(user.getId());

        return ResponseEntity.ok(topKeywords);
    }



}
