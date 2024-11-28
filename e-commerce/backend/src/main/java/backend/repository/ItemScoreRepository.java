package backend.repository;

import java.util.List;

import backend.entity.ItemScore;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemScoreRepository extends JpaRepository<ItemScore, Long> {

    @Query(value = """
        SELECT
            '수분' AS keyword, SUM(ABS(수분)) AS total
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '유분', SUM(ABS(유분)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '보습', SUM(ABS(보습)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '속건조', SUM(ABS(속건조)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '자극', SUM(ABS(자극)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '진정', SUM(ABS(진정)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '탄력', SUM(ABS(탄력)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '윤기', SUM(ABS(윤기)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '트러블', SUM(ABS(트러블)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '트러블개선', SUM(ABS(트러블개선)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '미백효과', SUM(ABS(미백효과)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '피부톤개선', SUM(ABS(피부톤개선)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '피부결개선', SUM(ABS(피부결개선)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '주름개선', SUM(ABS(주름개선)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '모공관리', SUM(ABS(모공관리)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '각질제거', SUM(ABS(각질제거)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '흡수력', SUM(ABS(흡수력)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '무게감', SUM(ABS(무게감)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '밀림', SUM(ABS(밀림)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '흘러내림', SUM(ABS(흘러내림)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '미끌거림', SUM(ABS(미끌거림)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '끈적임', SUM(ABS(끈적임)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '향', SUM(ABS(향)) 
        FROM item_score
        WHERE item_id IN :itemIds
        UNION ALL
        SELECT
            '양', SUM(ABS(양)) 
        FROM item_score
        WHERE item_id IN :itemIds
    """, nativeQuery = true)
    List<Object[]> findAllKeywordsByItemIds(@Param("itemIds") List<Long> itemIds);
}
