package backend.controller;
import backend.DTO.ItemDTO;
import backend.entity.User;
import backend.security.JwtUtil;
import backend.service.ReviewService;
import backend.service.UserService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * ReviewController 생성자: ReviewService를 주입받습니다.
     *
     * @param reviewService 리뷰 및 평점 관리를 위한 서비스
     */
    public ReviewController(ReviewService reviewService, UserService userService, JwtUtil jwtUtil) {
        this.reviewService = reviewService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 사용자가 구매한 제품 리스트를 반환합니다.
     *
     * @param token Autorization 사용자의 ID
     * @return 사용자가 구매한 제품 리스트
     */
    @GetMapping("/items")
    public List<ItemDTO> getReviewItems(@RequestHeader("Authorization") String token) {
        // JWT 토큰에서 사용자 이메일 추출
        String jwt = token.substring(7); // "Bearer " 제거
        String email = jwtUtil.extractUsername(jwt); // JWT에서 이메일 추출

        // 이메일로 사용자 ID 가져오기
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다."); // 예외 처리
        }

        // 사용자가 구매한 제품 리스트 반환
        return reviewService.getReviewItems(user.getId());
        /**
         * 특정 제품에 대한 리뷰와 평점을 저장합니다.
         *
         * @param reviewData 리뷰 데이터(JSON 형식)
         * @return 성공 메시지
         */
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveReview(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> reviewData
    ) {
        // JWT 토큰에서 사용자 이메일 추출
        String jwt = token.substring(7); // "Bearer " 제거
        String email = jwtUtil.extractUsername(jwt); // JWT에서 이메일 추출

        // 이메일로 사용자 ID 가져오기
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다."); // 예외 처리
        }

        // 요청 데이터에서 필요한 정보 추출
        Long itemId = ((Number) reviewData.get("itemId")).longValue();
        Integer rating = (Integer) reviewData.get("rating");
        String review = (String) reviewData.get("review");

        // Service 호출하여 데이터 저장
        reviewService.saveReview(user.getId(), itemId, rating, review);

        return ResponseEntity.ok("리뷰가 성공적으로 저장되었습니다.");
    }
}
