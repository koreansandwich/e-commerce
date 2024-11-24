package backend.repository;

import backend.DTO.ItemDTO;
import backend.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * ItemRepository는 items 테이블에 대한 데이터 액세스를 제공합니다.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * 특정 itemId 리스트에 해당하는 제품 정보를 조회합니다.
     *
     * @param itemIds 조회할 itemId 리스트
     * @return Item 엔티티 리스트
     */
    @Query("SELECT new backend.DTO.ItemDTO(i.itemId, i.itemName, i.itemImageUrl, i.itemLink, i.itemFinalPrice, i.brand) " +
            "FROM Item i WHERE i.itemId IN :itemIds")
    List<ItemDTO> findItemsByIds(@Param("itemIds") List<Long> itemIds);

    /**
     * 특정 상품 ID와 카테고리에 해당하는 추천 상품 리스트를 조회합니다.
     *
     * @param itemId   추천 대상 상품 ID
     * @param category 추천 상품 카테고리
     * @return 추천 상품 리스트
     */
    @Query("SELECT new backend.DTO.ItemDTO(i.itemId, i.itemName, i.itemImageUrl, i.itemLink, i.itemFinalPrice, i.brand) " +
            "FROM Item i JOIN SimilarRecommendation sr ON i.itemId = sr.recommendedItemId " +
            "WHERE sr.itemId = :itemId AND sr.category = :category")
    List<ItemDTO> findRecommendedItemsByItemIdAndCategory(@Param("itemId") Long itemId, @Param("category") String category);

}
