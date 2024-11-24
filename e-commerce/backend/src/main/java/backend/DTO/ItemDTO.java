package backend.DTO;

import lombok.Getter;
import lombok.Setter;

/**
 * ItemDTO는 필요한 필드만 클라이언트로 전달하기 위한 클래스입니다.
 */
@Setter
@Getter
public class ItemDTO {

    // Getters and Setters
    private Long itemId;
    private String itemName;
    private String itemLink;
    private String itemImageUrl;
    private Integer itemFinalPrice;
    private String brand;

    private UserReviewDTO userReview;
    private Boolean isPurchased;

    public ItemDTO(Long itemId, String itemName, String itemImageUrl, String itemLink, Integer itemFinalPrice, String brand) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemImageUrl = itemImageUrl;
        this.itemFinalPrice = itemFinalPrice;
        this.brand = brand;
        this.itemLink = itemLink;
    }

}
