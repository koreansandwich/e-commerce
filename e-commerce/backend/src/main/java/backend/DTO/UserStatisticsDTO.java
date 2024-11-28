package backend.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class UserStatisticsDTO {
    private int purchaseCount;
    private int reviewCount;
    private double averageRating;
    private Map<String, Integer> purchasedCategories;

    public UserStatisticsDTO(int purchaseCount, int reviewCount, double averageRating, Map<String, Integer> purchasedCategories) {
        this.purchaseCount = purchaseCount;
        this.reviewCount = reviewCount;
        this.averageRating = averageRating;
        this.purchasedCategories = purchasedCategories;
    }

}
