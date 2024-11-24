package backend.service;

import backend.DTO.ItemDTO;
import backend.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimilarRecommendationService {

    private final ItemRepository itemRepository; // Item 정보를 가져오는 Repository

    public SimilarRecommendationService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * 특정 상품에 대한 카테고리별 추천 상품을 가져옵니다.
     *
     * @param itemId 추천 대상 상품 ID
     * @return 카테고리별 추천 상품 리스트 (존재하지 않는 경우 빈 리스트 포함)
     */
    public Map<String, List<ItemDTO>> getRecommendedItemsByCategories(Long itemId) {
        // 카테고리 목록
        List<String> categories = List.of("cream", "lotion", "oil", "maskpack", "skin", "suncare", "essence", "mist");

        // 결과를 저장할 Map
        Map<String, List<ItemDTO>> recommendedItemsByCategory = new HashMap<>();

        for (String category : categories) {
            // 특정 카테고리에서 추천 상품 가져오기
            List<ItemDTO> recommendedItems = itemRepository.findRecommendedItemsByItemIdAndCategory(itemId, category);

            // 결과를 Map에 저장
            recommendedItemsByCategory.put(category, recommendedItems);
        }

        return recommendedItemsByCategory;
    }
}
