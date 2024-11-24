package backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "similar_recommendations") // 테이블 이름 매핑
public class SimilarRecommendation {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long itemId; // 추천 기준이 되는 아이템 ID

    @Column(nullable = false)
    private String category; // 제품 카테고리

    @Column(nullable = false)
    private Long recommendedItemId; // 추천된 아이템 ID

}
