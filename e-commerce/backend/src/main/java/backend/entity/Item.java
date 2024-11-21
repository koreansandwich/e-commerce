package backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "items") // 테이블 이름을 명시적으로 지정
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT와 매핑
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "item_link")
    private String itemLink;

    @Column(name = "item_original_price")
    private Integer itemOriginalPrice;

    @Column(name = "item_discount_rate")
    private Float itemDiscountRate;

    @Column(name = "item_final_price")
    private Integer itemFinalPrice;

    @Column(name = "brand")
    private String brand;

    @Column(name = "item_type")
    private String itemType;

    @Column(name = "item_image_url", length = 2083)
    private String itemImageUrl;

    // 기본 생성자 (JPA 사용을 위해 필수)
    public Item() {}

    // 생성자
    public Item(String itemName, String itemLink, Integer itemOriginalPrice, Float itemDiscountRate,
                Integer itemFinalPrice, String brand, String itemType, String itemImageUrl) {
        this.itemName = itemName;
        this.itemLink = itemLink;
        this.itemOriginalPrice = itemOriginalPrice;
        this.itemDiscountRate = itemDiscountRate;
        this.itemFinalPrice = itemFinalPrice;
        this.brand = brand;
        this.itemType = itemType;
        this.itemImageUrl = itemImageUrl;
    }

    // Getters and Setters
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemLink() {
        return itemLink;
    }

    public void setItemLink(String itemLink) {
        this.itemLink = itemLink;
    }

    public Integer getItemOriginalPrice() {
        return itemOriginalPrice;
    }

    public void setItemOriginalPrice(Integer itemOriginalPrice) {
        this.itemOriginalPrice = itemOriginalPrice;
    }

    public Float getItemDiscountRate() {
        return itemDiscountRate;
    }

    public void setItemDiscountRate(Float itemDiscountRate) {
        this.itemDiscountRate = itemDiscountRate;
    }

    public Integer getItemFinalPrice() {
        return itemFinalPrice;
    }

    public void setItemFinalPrice(Integer itemFinalPrice) {
        this.itemFinalPrice = itemFinalPrice;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }
}
