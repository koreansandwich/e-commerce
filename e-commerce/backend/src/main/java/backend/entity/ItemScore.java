package backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "item_score")
public class ItemScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id") // 테이블의 item_id를 PK로 설정
    private Long itemId;

    @Column(name = "수분", nullable = false)
    private int moisture;

    @Column(name = "유분", nullable = false)
    private int oiliness;

    @Column(name = "보습", nullable = false)
    private int hydration;

    @Column(name = "속건조", nullable = false)
    private int dryness;

    @Column(name = "자극", nullable = false)
    private int irritation;

    @Column(name = "진정", nullable = false)
    private int soothing;

    @Column(name = "탄력", nullable = false)
    private int elasticity;

    @Column(name = "윤기", nullable = false)
    private int glossiness;

    @Column(name = "트러블", nullable = false)
    private int acne;

    @Column(name = "트러블개선", nullable = false)
    private int acneImprovement;

    @Column(name = "미백효과", nullable = false)
    private int whiteningEffect;

    @Column(name = "피부톤개선", nullable = false)
    private int toneImprovement;

    @Column(name = "피부결개선", nullable = false)
    private int textureImprovement;

    @Column(name = "주름개선", nullable = false)
    private int wrinkleImprovement;

    @Column(name = "모공관리", nullable = false)
    private int poreCare;

    @Column(name = "각질제거", nullable = false)
    private int exfoliation;

    @Column(name = "흡수력", nullable = false)
    private int absorption;

    @Column(name = "무게감", nullable = false)
    private int weight;

    @Column(name = "밀림", nullable = false)
    private int clumping;

    @Column(name = "흘러내림", nullable = false)
    private int dripping;

    @Column(name = "미끌거림", nullable = false)
    private int slipperiness;

    @Column(name = "끈적임", nullable = false)
    private int stickiness;

    @Column(name = "향", nullable = false)
    private int fragrance;

    @Column(name = "양", nullable = false)
    private int quantity;
}
