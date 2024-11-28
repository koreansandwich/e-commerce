import React, { useState, useEffect } from "react";
import axios from "axios";
import "./DashBoard.css";
import { PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, Tooltip, Legend } from "recharts";

const DashBoard = () => {
    const [recommendations, setRecommendations] = useState([]);
    const [items, setItems] = useState([]); // 구매한 제품 리스트
    const [profile, setProfile] = useState(null);
    const [statistics, setStatistics] = useState(null); // 사용자 통계 데이터
    const COLORS = ["#8884d8", "#82ca9d", "#ffc658", "#ff8042", "#0088FE"]; // 차트 색상
    const [ratingData, setRatingData] = useState([]); // 별점 데이터를 저장할 상태
    const [topKeywords, setTopKeywords] = useState([]);

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

        axios
            .get("http://localhost:8080/api/dashboard/statistics", {
                headers: { Authorization: `Bearer ${token}` },
            })
            .then((response) => {
                setStatistics(response.data); // 통계 데이터 저장
            })
            .catch((err) => {
                console.error("사용자 통계를 가져오는 데 실패했습니다:", err);
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
        axios
            .get("http://localhost:8080/api/dashboard/rating-distribution", {
                headers: { Authorization: `Bearer ${token}` },
            })
            .then((response) => {
                const data = Object.entries(response.data).map(([key, value]) => ({
                    name: `${key}점`,
                    value: value,
                }));
                setRatingData(data); // 데이터를 상태로 저장
            })
            .catch((err) => console.error("별점 분포 데이터를 가져오는 데 실패했습니다:", err));

        axios
            .get("http://localhost:8080/api/dashboard/top-keywords", {
                headers: { Authorization: `Bearer ${token}` },
            })
            .then((response) => setTopKeywords(response.data))
            .catch((err) => console.error("키워드 데이터를 가져오는 데 실패했습니다:", err));
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
                        src={"https://koreansandwich-ecommerce-item-image.s3.ap-southeast-2.amazonaws.com/cute-profile-image.png"}
                        alt="프로필"
                        onError={(e) => (e.target.src = "/default-profile.png")}
                    />
                    <div className="profile-info">
                        <br/>
                        <p>{profile.name}</p>
                        <p>{profile.age} / {profile.gender}</p>
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

            {/* 통계 섹션 */}
            {statistics && (
                <div className="statistics-section">
                    {/* 구매 통계 칸 */}
                    <div className="statistics-card">
                        <h3>구매 통계</h3>
                        <p>구매 횟수: {statistics.purchaseCount}</p>
                        <p>리뷰 개수: {statistics.reviewCount}</p>
                        <p>평균 별점: {statistics.averageRating.toFixed(2)}</p>
                    </div>

                    {/* 구매한 제품 카테고리 칸 */}
                    <div className="chart-card">
                        <h3>구매한 제품 카테고리</h3>
                        <div className="chart-container">
                            <PieChart width={200} height={200}>
                                <Pie
                                    data={Object.entries(statistics.purchasedCategories).map(([key, value]) => ({
                                        name: key,
                                        value: value,
                                    }))}
                                    dataKey="value"
                                    nameKey="name"
                                    cx="50%"
                                    cy="50%"
                                    outerRadius={80}
                                    fill="#8884d8"
                                >
                                    {Object.keys(statistics.purchasedCategories).map((_, index) => (
                                        <Cell
                                            key={`cell-${index}`}
                                            fill={COLORS[index % COLORS.length]}
                                        />
                                    ))}
                                </Pie>
                                <Tooltip />
                                <Legend
                                    layout="horizontal"
                                    align="center"
                                    verticalAlign="bottom"
                                    wrapperStyle={{ fontSize: '12px' }} />
                            </PieChart>

                        </div>
                    </div>

                    {/* 평균 별점 분포 칸 */}
                    <div className="chart-card">
                        <h3>평균 별점 분포</h3>
                        <div className="chart-container">
                            <BarChart
                                width={200}
                                height={200}
                                data={ratingData}
                            >
                                <XAxis dataKey="name" />
                                <YAxis />
                                <Tooltip />
                                <Bar dataKey="value" fill="#82ca9d" />
                            </BarChart>
                        </div>
                    </div>

                    <div className="keyword-card">
                        <h3>선호 키워드</h3>
                        {topKeywords.length > 0 ? (
                            <ul>
                                {topKeywords.map((keyword, index) => (
                                    <li key={index}>{index + 1}위: {keyword}</li>
                                ))}
                            </ul>
                        ) : (
                            <p>데이터 없음</p>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

export default DashBoard;
