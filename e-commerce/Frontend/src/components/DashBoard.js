import React, { useState, useEffect } from "react";
import axios from "axios";
import "./DashBoard.css";

const DashBoard = () => {
    const [recommendations, setRecommendations] = useState([]);
    const [items, setItems] = useState([]); // 구매한 제품 리스트
    const [profile, setProfile] = useState(null);

    useEffect(() => {
        const token = localStorage.getItem("token"); // JWT 토큰 가져오기
        if (!token) {
            alert("로그인이 필요합니다. 다시 로그인해 주세요.");
            window.location.href = "/login";
            return;
        }

        // 추천 제품 가져오기
        axios
            .get("http://localhost:8080/api/dashboard/recommendations_user", {
                headers: { Authorization: `Bearer ${token}` },
            })
            .then((response) => {
                setRecommendations(response.data);
            })
            .catch((err) => {
                console.error("추천 정보를 가져오는 데 실패했습니다:", err);
            });

        // 프로필 정보 가져오기
        axios
            .get("http://localhost:8080/api/dashboard/profile", {
                headers: { Authorization: `Bearer ${token}` },
            })
            .then((response) => {
                setProfile(response.data);
            })
            .catch((err) => {
                console.error("프로필 정보를 가져오는 데 실패했습니다:", err);
            });
    }, []);

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



    return (
        <div className="dashboard-container">
            {/* 프로필 섹션 */}
            {profile && (
                <div className="profile-section">
                    <img
                        src={profile?.imageUrl || "/default-profile.png"}
                        alt="프로필"
                        onError={(e) => (e.target.src = "/default-profile.png")}
                    />
                    <div className="profile-info">
                        <p>이름: {profile.name}</p>
                        <p>나이: {profile.age}</p>
                        <p>성별: {profile.gender}</p>
                    </div>
                </div>
            )}
            {/* 추천 제품 섹션 */}
            <div className="recommendations-section">
                <h2>추천 제품</h2>
                <div className="items-grid">
                    {recommendations.map((item) => (
                        <div key={item.itemId} className="item-card">
                            <img
                                src={item.itemImageUrl}
                                alt={item.itemName}
                                onClick={() => window.open(item.itemLink, "_blank")}
                                style={{cursor: "pointer"}} // 이미지 클릭 시 커서 표시
                            />
                            <h3>{item.itemName}</h3>
                            <p>가격: {item.itemFinalPrice}원</p>
                            <button
                                onClick={() => handleConfirmPurchase(item.itemId)}
                                className="purchase-button"
                            >
                                구매 확정
                            </button>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default DashBoard;
