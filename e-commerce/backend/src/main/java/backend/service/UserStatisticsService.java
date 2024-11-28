package backend.service;

import backend.DTO.UserStatisticsDTO;
import backend.repository.ItemScoreRepository;
import backend.repository.UserHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserStatisticsService {

    private final UserHistoryRepository userHistoryRepository;
    private final ItemScoreRepository itemScoreRepository;

    public UserStatisticsService(UserHistoryRepository userHistoryRepository, ItemScoreRepository itemScoreRepository) {
        this.userHistoryRepository = userHistoryRepository;
        this.itemScoreRepository = itemScoreRepository;

    }

    public UserStatisticsDTO getUserStatistics(Long userId) {
        // 기존 통계 데이터 조회
        List<Object[]> rawStatistics = userHistoryRepository.findUserStatisticsRaw(userId);

        if (rawStatistics.isEmpty()) {
            return new UserStatisticsDTO(0, 0, 0.0, new HashMap<>());
        }

        // 기존 통계 데이터 처리 (구매 횟수, 리뷰 개수, 평균 별점)
        int purchaseCount = 0;
        int reviewCount = 0;
        double averageRating = 0.0;

        for (Object[] row : rawStatistics) {
            purchaseCount = ((Number) row[0]).intValue();
            reviewCount = ((Number) row[1]).intValue();
            averageRating = ((Number) row[2]).doubleValue();
        }

        // 카테고리별 구매 데이터 추가 조회
        Map<String, Integer> purchasedCategories = getPurchasedCategories(userId);

        return new UserStatisticsDTO(purchaseCount, reviewCount, averageRating, purchasedCategories);
    }

    /**
     * 카테고리별 구매 데이터를 조회하고 반환합니다.
     *
     * @param userId 사용자 ID
     * @return 카테고리별 구매 수 (Map<String, Integer>)
     */
    private Map<String, Integer> getPurchasedCategories(Long userId) {
        List<Object[]> rawCategories = userHistoryRepository.findPurchasedCategories(userId);

        Map<String, Integer> purchasedCategories = new HashMap<>();
        for (Object[] row : rawCategories) {
            String category = (String) row[0];
            int count = ((Number) row[1]).intValue();
            purchasedCategories.put(category, count);
        }

        return purchasedCategories;
    }


    public Map<Integer, Integer> getRatingDistribution(Long userId) {
        List<Object[]> rawDistribution = userHistoryRepository.findRatingDistribution(userId);

        Map<Integer, Integer> ratingDistribution = new HashMap<>();
        for (Object[] row : rawDistribution) {
            Integer rating = ((Number) row[0]).intValue();
            Integer count = ((Number) row[1]).intValue();
            ratingDistribution.put(rating, count);
        }

        return ratingDistribution;
    }

    public List<String> getTopKeywords(Long userId) {
        // 사용자가 구매한 item_id 가져오기
        List<Long> itemIds = userHistoryRepository.findItemIdsByUserId(userId);

        if (itemIds.isEmpty()) {
            return List.of("데이터 없음");
        }

        // item_score 테이블에서 모든 키워드 점수 가져오기
        List<Object[]> rawKeywords = itemScoreRepository.findAllKeywordsByItemIds(itemIds);

        // 디버깅 출력: 모든 키워드와 점수 확인
        System.out.println("[DEBUG] All Keywords and Scores:");
        for (Object[] row : rawKeywords) {
            System.out.println("[DEBUG] Keyword: " + row[0] + ", Score: " + row[1]);
        }

        // 상위 3개 키워드 추출
        List<Object[]> topKeywords = rawKeywords.stream()
                .sorted((a, b) -> ((Number) b[1]).intValue() - ((Number) a[1]).intValue())
                .limit(3)
                .toList();

        // 디버깅 출력: 상위 3개 키워드 확인
        System.out.println("[DEBUG] Top 3 Keywords:");
        for (Object[] row : topKeywords) {
            System.out.println("[DEBUG] Keyword: " + row[0] + ", Score: " + row[1]);
        }

        // 결과 매핑 (키워드명만 추출)
        return topKeywords.stream()
                .map(row -> (String) row[0]) // 키워드명 추출
                .collect(Collectors.toList());
    }
}
