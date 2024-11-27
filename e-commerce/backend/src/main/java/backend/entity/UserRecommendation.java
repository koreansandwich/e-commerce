package backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "recommendations")
public class UserRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "item_type", nullable = false)
    private String itemType;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "age_group", nullable = false)
    private String ageGroup;

    @Column(name = "score", nullable = false)
    private Float score;

    @Column(name = "avg_rating", nullable = false)
    private Float avgRating;

    @Column(name = "review_count", nullable = false)
    private Integer reviewCount;

    // 기본 생성자
    public UserRecommendation() {}

    // 생성자
    public UserRecommendation(Long itemId, String itemType, String gender, String ageGroup, Float score, Float avgRating, Integer reviewCount) {
        this.itemId = itemId;
        this.itemType = itemType;
        this.gender = gender;
        this.ageGroup = ageGroup;
        this.score = score;
        this.avgRating = avgRating;
        this.reviewCount = reviewCount;
    }

}
