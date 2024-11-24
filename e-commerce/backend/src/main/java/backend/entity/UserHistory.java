package backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "user_history")
public class UserHistory {
    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratingId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long itemId;

    @Column
    private Integer rating;

    @Column
    private String review;

    @Column(name = "is_Purchased", nullable = false)
    private Boolean isPurchased = false;

}
