package backend.repository;

import backend.entity.UserRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRecommendationRepository extends JpaRepository<UserRecommendation, Long> {

    @Query("SELECT r FROM UserRecommendation r WHERE r.gender = :gender AND r.ageGroup = :ageGroup AND r.itemType = :itemType ORDER BY r.score DESC")
    List<UserRecommendation> findTopRecommendationsByGenderAndAgeGroupAndItemType(
            @Param("gender") String gender,
            @Param("ageGroup") String ageGroup,
            @Param("itemType") String itemType
    );

    @Query("SELECT DISTINCT r.itemType FROM UserRecommendation r WHERE r.gender = :gender AND r.ageGroup = :ageGroup")
    List<String> findDistinctItemTypesByGenderAndAgeGroup(
            @Param("gender") String gender,
            @Param("ageGroup") String ageGroup
    );
}
