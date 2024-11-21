package backend.DTO;

import lombok.Getter;
import lombok.Setter;

/**
 * ItemDTO는 필요한 필드만 클라이언트로 전달하기 위한 클래스입니다.
 */
public class ItemDTO {

    // Getters and Setters
    @Setter
    @Getter
    private Long itemId;
    @Setter
    @Getter
    private String itemName;
    @Setter
    @Getter
    private String itemLink;
    @Setter
    @Getter
    private String itemImageUrl;
    @Setter
    @Getter
    private Integer itemFinalPrice;
    @Setter
    @Getter
    private String brand;

    public ItemDTO(Long itemId, String itemName, String itemImageUrl, String itemLink, Integer itemFinalPrice, String brand) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemImageUrl = itemImageUrl;
        this.itemFinalPrice = itemFinalPrice;
        this.brand = brand;
        this.itemLink = itemLink;
    }

}
