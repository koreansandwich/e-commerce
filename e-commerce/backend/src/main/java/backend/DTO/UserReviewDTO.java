package backend.DTO;

public class UserReviewDTO {

    private Long itemId;      // 리뷰 대상 제품 ID
    private Integer rating;   // 별점
    private String review;    // 리뷰 내용

    public UserReviewDTO(Long itemId, Integer rating, String review) {
        this.itemId = itemId;
        this.rating = rating;
        this.review = review;
    }

    // Getters and Setters
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}

