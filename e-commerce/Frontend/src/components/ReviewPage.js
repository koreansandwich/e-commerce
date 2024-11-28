import React, { useState, useEffect } from "react";
import axios from "axios";
import styles from "./ReviewPage.module.css";

const ReviewPage = () => {
    const [items, setItems] = useState([]); // 구매한 제품 리스트
    const [selectedItem, setSelectedItem] = useState(null); // 리뷰를 남길 선택된 제품
    const [rating, setRating] = useState(0); // 리뷰 별점
    const [review, setReview] = useState(""); // 리뷰 내용
    const [error, setError] = useState(""); // 에러 메시지
    const [recommendations, setRecommendations] = useState({}); // 추천 제품
    const [showRecommendations, setShowRecommendations] = useState(false); // 추천 제품 모달 상태
    const [minHeight, setMinHeight] = useState("100vh");

    useEffect(() => {
        const token = localStorage.getItem("token"); // JWT 토큰 가져오기
        if (!token) {
            alert("로그인이 필요합니다. 다시 로그인해 주세요.");
            window.location.href = "/login";
            return;
        }

        // 구매한 제품 리스트 가져오기
        axios
            .get("http://localhost:8080/api/review/items", {
                headers: {Authorization: `Bearer ${token}`},
            })
            .then((response) => {
                setItems(response.data);
            })
            .catch((err) => {
                console.error("구매 제품 정보를 가져오는 데 실패했습니다:", err);
                setError("구매 제품 정보를 가져오는 데 실패했습니다.");
            });
        if (selectedItem) {
            setRating(selectedItem.userReview?.rating || 0); // 기존 별점 기본값 설정
            setReview(selectedItem.userReview?.review || ""); // 기존 리뷰 기본값 설정
        }

        const updateMinHeight = () => {
            const pageHeight = document.body.scrollHeight;
            const viewportHeight = window.innerHeight;
            const calculatedHeight = Math.max(viewportHeight, pageHeight);
            setMinHeight(`${calculatedHeight * 1.4}px`);
        };

        updateMinHeight();
        window.addEventListener("resize", updateMinHeight);

        return () => window.removeEventListener("resize", updateMinHeight);

    }, [selectedItem]);

    const handleOpenRecommendations = (itemId) => {
        const token = localStorage.getItem("token");
        axios
            .get(`http://localhost:8080/api/review/${itemId}/recommendations`, {
                headers: {Authorization: `Bearer ${token}`},
            })
            .then((response) => {
                setRecommendations(response.data);
                setShowRecommendations(true);
            })
            .catch((err) => {
                console.error("추천 제품 정보를 가져오는 데 실패했습니다:", err);
                alert("추천 제품 정보를 가져오는 데 실패했습니다.");
            });
    };

    const handleCloseRecommendations = () => {
        setShowRecommendations(false);
        setRecommendations({});
    };

    const handleOpenReviewModal = (item) => {
        setSelectedItem(item);
        setRating(item.userReview?.rating || 0); // 기존 별점이 있으면 기본값으로 설정
        setReview(item.userReview?.review || ""); // 기존 리뷰가 있으면 기본값으로 설정
    };

    const handleSaveReview = () => {
        if (!rating || !review.trim()) {
            alert("별점과 리뷰를 모두 입력해주세요.");
            return;
        }

        const token = localStorage.getItem("token");
        axios
            .post(
                "http://localhost:8080/api/review/save",
                {
                    userId: selectedItem.userId,
                    itemId: selectedItem.itemId,
                    rating,
                    review,
                },
                {headers: {Authorization: `Bearer ${token}`}}
            )
            .then(() => {
                alert("리뷰가 성공적으로 저장되었습니다.");
                setSelectedItem(null); // 모달 닫기
                setItems((prevItems) =>
                    prevItems.map((item) =>
                        item.itemId === selectedItem.itemId
                            ? {...item, rating, review}
                            : item
                    )
                );
            })
            .catch((err) => {
                console.error("리뷰 저장 실패:", err);
                alert("리뷰를 저장하는 데 실패했습니다. 다시 시도해주세요.");
            });
    };

    const handleConfirmPurchase = (itemId) => {
        const token = localStorage.getItem("token");
        axios
            .post(
                "http://localhost:8080/api/review/confirm",
                {itemId},
                {headers: {Authorization: `Bearer ${token}`}}
            )
            .then(() => {
                alert("구매가 성공적으로 확정되었습니다.");
                setItems((prevItems) =>
                    prevItems.map((item) =>
                        item.itemId === itemId
                            ? {...item, isPurchased: true}
                            : item
                    )
                );
            })
            .catch((err) => {
                console.error("구매 확정 실패:", err);
                alert("구매를 확정하는 데 실패했습니다.");
            });
    };


    const handleCloseReviewModal = () => {
        setSelectedItem(null); // 선택된 아이템 초기화
    };


    return (
        <div className={styles.reviewPageContainer}
             style={{
                 background: "linear-gradient(to bottom, #6c63ff, #c6bffb)",
                 minHeight,
                 padding: "20px",
                 display: "flex",
                 flexDirection: "column",
                 alignItems: "center",
                 gap: "20px",
             }}>
            {error && <p className={styles.errorMessage}>{error}</p>}
            <div className={styles.itemsGrid}>
                {items.map((item) => (
                    <div key={item.itemId} className={styles.itemCard}>
                        <img
                            src={item.itemImageUrl}
                            alt={item.itemName}
                            className={styles.itemImage}
                            onClick={() => window.open(item.itemLink, "_blank")}
                            style={{ cursor: "pointer" }}
                        />
                        <h3 className={styles.itemTitle}>{item.itemName}</h3>
                        <p className={styles.itemDetails}>
                            가격: {item.itemFinalPrice}원
                        </p>
                        <p className={styles.itemDetails}>
                            브랜드: {item.brand}
                        </p>
                        <button
                            onClick={() => handleConfirmPurchase(item.itemId)}
                            className={styles.purchaseButton}
                            disabled={item.isPurchased}
                        >
                            {item.isPurchased ? "구매 확정 완료" : "구매 확정"}
                        </button>
                        <button
                            onClick={() => handleOpenReviewModal(item)}
                            className={styles.reviewButton}
                        >
                            리뷰 남기기
                        </button>
                        <button
                            onClick={() => handleOpenRecommendations(item.itemId)}
                            className={styles.recommendationButton}
                        >
                            추천 제품 보기
                        </button>
                    </div>
                ))}
            </div>

            {/* 리뷰 모달 */}
            {selectedItem && (
                <div className={styles.modal}>
                    <div className={styles.modalContent}>
                        <h3>{selectedItem.itemName}</h3>
                        <p>별점을 입력해주세요:</p>
                        <div className={styles.ratingInput}>
                            {[1, 2, 3, 4, 5].map((star) => (
                                <span
                                    key={star}
                                    className={`${styles.star} ${
                                        star <= rating ? styles.selected : ""
                                    }`}
                                    onClick={() => setRating(star)}
                                >
                                    ★
                                </span>
                            ))}
                        </div>
                        <textarea
                            value={review}
                            onChange={(e) => setReview(e.target.value)}
                            placeholder="리뷰를 작성해주세요"
                            className={styles.reviewTextarea}
                        />
                        <div className={styles.modalButtons}>
                            <button
                                onClick={handleSaveReview}
                                className={styles.saveButton}
                            >
                                저장하기
                            </button>
                            <button
                                onClick={handleCloseReviewModal}
                                className={styles.cancelButton}
                            >
                                취소
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* 추천 모달 */}
            {showRecommendations && recommendations && Object.keys(recommendations).length > 0 && (
                <div className={styles.recommendationModal}>
                    <div className={styles.recommendationModalContent}>
                        <div className={styles.recommendationGrid}>
                            {Object.entries(recommendations).map(
                                ([category, products]) => {
                                    const product = products[0];
                                    if (!product) return null;
                                    return (
                                        <div
                                            key={category}
                                            className={
                                                styles.recommendationItem
                                            }
                                            onClick={() =>
                                                window.open(
                                                    product.itemLink,
                                                    "_blank"
                                                )
                                            }
                                        >
                                            <img
                                                src={product.itemImageUrl}
                                                alt={product.itemName}
                                                className={
                                                    styles.recommendationImage
                                                }
                                            />
                                            <p
                                                onClick={(e) => e.stopPropagation()}
                                            >{product.itemName}</p>
                                            <button
                                                onClick={(e) => {
                                                    e.stopPropagation();
                                                    handleConfirmPurchase(
                                                        product.itemId
                                                    )
                                                }}
                                                className={
                                                    styles.purchaseButton
                                                }
                                                disabled={product.isPurchased}
                                            >
                                                {product.isPurchased
                                                    ? "구매 확정 완료"
                                                    : "구매 확정"}
                                            </button>
                                        </div>
                                    );
                                }
                            )}
                        </div>
                        <button
                            onClick={handleCloseRecommendations}
                            className={styles.cancelButton}
                        >
                            닫기
                        </button>
                    </div>
                </div>
            )}
        </div>

    );
};

export default ReviewPage;