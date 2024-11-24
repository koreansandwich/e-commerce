import React, { useState, useEffect } from "react";
import axios from "axios";
import "./ReviewPage.css";

const ReviewPage = () => {
    const [items, setItems] = useState([]); // 구매한 제품 리스트
    const [selectedItem, setSelectedItem] = useState(null); // 리뷰를 남길 선택된 제품
    const [rating, setRating] = useState(0); // 리뷰 별점
    const [review, setReview] = useState(""); // 리뷰 내용
    const [error, setError] = useState(""); // 에러 메시지

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
                headers: { Authorization: `Bearer ${token}` },
            })
            .then((response) => {
                setItems(response.data);
            })
            .catch((err) => {
                console.error("구매 제품 정보를 가져오는 데 실패했습니다:", err);
                setError("구매 제품 정보를 가져오는 데 실패했습니다.");
            });
    }, []);

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
                { headers: { Authorization: `Bearer ${token}` } }
            )
            .then(() => {
                alert("리뷰가 성공적으로 저장되었습니다.");
                setSelectedItem(null); // 모달 닫기
                setItems((prevItems) =>
                    prevItems.map((item) =>
                        item.itemId === selectedItem.itemId
                            ? { ...item, rating, review }
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
                { itemId },
                { headers: { Authorization: `Bearer ${token}` } }
            )
            .then(() => {
                alert("구매가 성공적으로 확정되었습니다.");
                setItems((prevItems) =>
                    prevItems.map((item) =>
                        item.itemId === itemId
                            ? { ...item, isPurchased: true }
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
        <div className="review-page-container">
            <h2>구매 제품 리뷰</h2>
            {error && <p className="error-message">{error}</p>}
            <div className="items-grid">
                {items.map((item) => (
                    <div key={item.itemId} className="item-card">
                        <img
                            src={item.itemImageUrl}
                            alt={item.itemName}
                            className="item-image"
                            onClick={() => window.open(item.itemLink, "_blank")} // 새 탭에서 링크 열기
                            style={{ cursor: "pointer" }} // 마우스 포인터 변경
                        />
                        <h3>{item.itemName}</h3>
                        <p>가격: {item.itemFinalPrice}원</p>
                        <p>브랜드: {item.brand}</p>
                            <button
                                onClick={() => handleConfirmPurchase(item.itemId)}
                                className="purchase-button"
                                disabled={item.isPurchased} // 구매 확정된 경우 버튼 비활성화
                            >
                                {item.isPurchased ? "구매 확정 완료" : "구매 확정"}
                            </button>
                            <button
                                onClick={() => handleOpenReviewModal(item)}
                                className="review-button"
                            >
                                리뷰 남기기
                            </button>
                    </div>
                ))}
            </div>

            {/* 리뷰 모달 */}
            {selectedItem && (
                <div className="modal">
                    <div className="modal-content">
                        <h3>{selectedItem.itemName}</h3>
                        <p>별점을 입력해주세요:</p>
                        <div className="rating-input">
                            {[1, 2, 3, 4, 5].map((star) => (
                                <span
                                    key={star}
                                    className={`star ${star <= rating ? "selected" : ""}`}
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
                            className="review-textarea"
                        />
                        <div className="modal-buttons">
                            <button onClick={handleSaveReview} className="save-button">
                                저장하기
                            </button>
                            <button onClick={handleCloseReviewModal} className="cancel-button">
                                취소
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );

}

export default ReviewPage;