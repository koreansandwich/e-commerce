package backend.repository;

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


}
