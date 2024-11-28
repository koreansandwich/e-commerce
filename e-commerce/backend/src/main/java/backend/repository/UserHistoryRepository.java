package backend.repository;

import backend.DTO.UserStatisticsDTO;
import backend.entity.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * UserHistoryRepository는 user_history 테이블에 대한 데이터 액세스를 제공합니다.
 */
public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {

    /**
     * 특정 사용자가 구매한 제품 ID 리스트를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자가 구매한 itemId 리스트
     */
    @Query("SELECT uh.itemId FROM UserHistory uh WHERE uh.userId = :userId")
    List<Long> findItemIdsByUserId(@Param("userId") Long userId);

    /**
     * 특정 사용자와 제품 ID에 해당하는 구매 기록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @param itemId 제품 ID
     * @return UserHistory Optional 객체
     */
    @Query("SELECT uh FROM UserHistory uh WHERE uh.userId = :userId AND uh.itemId = :itemId")
    Optional<UserHistory> findByUserIdAndItemId(@Param("userId") Long userId, @Param("itemId") Long itemId);

    @Modifying
    @Query("UPDATE UserHistory uh SET uh.isPurchased = :status WHERE uh.userId = :userId AND uh.itemId = :itemId")
    void updatePurchaseStatus(@Param("userId") Long userId, @Param("itemId") Long itemId, @Param("status") Boolean status);


    @Modifying
    @Query("DELETE FROM UserHistory uh WHERE uh.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT " +
            "COUNT(DISTINCT uh.rating_id) AS purchaseCount, " +
            "COUNT(uh.review) AS reviewCount, " +
            "COALESCE(AVG(uh.rating), 0) AS averageRating " +
            "FROM user_history uh " +
            "WHERE uh.user_id = :userId", nativeQuery = true)
    List<Object[]> findUserStatisticsRaw(@Param("userId") Long userId);


    @Query(value = "SELECT " +
            "i.item_type AS category, " +
            "COUNT(uh.item_id) AS count " +
            "FROM user_history uh " +
            "JOIN items i ON uh.item_id = i.item_id " +
            "WHERE uh.user_id = :userId " +
            "GROUP BY i.item_type", nativeQuery = true)
    List<Object[]> findPurchasedCategories(@Param("userId") Long userId);

    @Query(value = "SELECT uh.rating AS rating, COUNT(*) AS count " +
            "FROM user_history uh " +
            "WHERE uh.user_id = :userId AND uh.rating IS NOT NULL " +
            "GROUP BY uh.rating " +
            "ORDER BY uh.rating", nativeQuery = true)
    List<Object[]> findRatingDistribution(@Param("userId") Long userId);





}
