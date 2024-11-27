package backend.service;

import backend.entity.UserRecommendation;
import backend.repository.UserRecommendationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserRecommendationService {

    private final UserRecommendationRepository userRecommendationRepository;

    public UserRecommendationService(UserRecommendationRepository userRecommendationRepository) {
        this.userRecommendationRepository = userRecommendationRepository;
    }

    public List<UserRecommendation> getRecommendations(String gender, String ageGroup, String itemType) {
        System.out.println("[DEBUG] Fetching recommendations for Gender: " + gender + ", Age Group: " + ageGroup + ", Item Type: " + itemType);
        List<UserRecommendation> recommendations = userRecommendationRepository.findTopRecommendationsByGenderAndAgeGroupAndItemType(gender, ageGroup, itemType);
        System.out.println("[DEBUG] Retrieved Recommendations: " + recommendations);
        return recommendations;
    }

    public List<UserRecommendation> getDistinctItemTypes(String gender, String ageGroup) {
        System.out.println("[DEBUG] Fetching top recommendations for all predefined categories.");

        // 8개의 고정된 카테고리 리스트
        List<String> predefinedCategories = List.of("cream", "lotion", "oil", "maskpack", "skin", "suncare", "essence", "mist");
        List<UserRecommendation> topRecommendations = new ArrayList<>();

        for (String itemType : predefinedCategories) {
            System.out.println("[DEBUG] Fetching top recommendation for Category: " + itemType);
            List<UserRecommendation> recommendations = userRecommendationRepository.findTopRecommendationsByGenderAndAgeGroupAndItemType(gender, ageGroup, itemType);
            if (!recommendations.isEmpty()) {
                topRecommendations.add(recommendations.get(0)); // 각 카테고리의 최고 점수 1개만 추가
                System.out.println("[DEBUG] Added top recommendation: " + recommendations.get(0));
            } else {
                System.out.println("[DEBUG] No recommendation found for Category: " + itemType);
            }
        }
        System.out.println("[DEBUG] Final Top Recommendations: " + topRecommendations);
        return topRecommendations;
    }

}
