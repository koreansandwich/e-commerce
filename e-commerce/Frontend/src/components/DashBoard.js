import React, { useState, useEffect } from "react";
import axios from "axios";
import styles from "./DashBoard.module.css";
import { PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, Tooltip, Legend } from "recharts";

const DashBoard = () => {
    const [recommendations, setRecommendations] = useState([]);
    const [items, setItems] = useState([]); // 구매한 제품 리스트
    const [profile, setProfile] = useState(null);
    const [statistics, setStatistics] = useState(null); // 사용자 통계 데이터
    const [ratingData, setRatingData] = useState([]); // 별점 데이터를 저장할 상태
    const [topKeywords, setTopKeywords] = useState([]);
    const COLORS = ["#8884d8", "#82ca9d", "#ffc658", "#ff8042", "#0088FE"]; // 차트 색상
    const [isLoading, setIsLoading] = useState(true);

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
                setIsLoading(false);
            })
            .catch((err) => {
                console.error("추천 정보를 가져오는 데 실패했습니다:", err);
                setIsLoading(false);
            });

        // 사용자 통계 가져오기
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
                // 서버에서 받은 데이터 변환
                const initialData = Object.entries(response.data).map(([key, value]) => ({
                    name: `${key}점`,
                    value: value,
                }));

                // 1점 ~ 5점의 기본 구조를 유지하며, 값이 없으면 0으로 설정
                const completeData = ["1점", "2점", "3점", "4점", "5점"].map((point) => {
                    const existingData = initialData.find((data) => data.name === point);
                    return existingData ? existingData : { name: point, value: 0 };
                });

                setRatingData(completeData); // 변환된 데이터를 상태에 저장
            })
            .catch((err) => console.error("별점 분포 데이터를 가져오는 데 실패했습니다:", err));


        // 선호 키워드 가져오기
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

    return (
        <div className={styles.dashboardContainer}>
            {/* 프로필 섹션 */}
            {profile && (
                <div className={styles.profileSection}>
                    <img
                        src={
                            "https://koreansandwich-ecommerce-item-image.s3.ap-southeast-2.amazonaws.com/cute-profile-image.png"
                        }
                        alt="프로필"
                        onError={(e) => (e.target.src = "/default-profile.png")}
                    />
                    <div className={styles.profileInfo}>
                        <p>{profile.name}</p>
                        <p>
                            {profile.age} / {profile.gender}
                        </p>
                    </div>
                </div>
            )}

            {/* 추천 제품 섹션 */}
            <div className={styles.recommendationsSection}>
                <h2>추천 제품</h2>
                {isLoading ? (
                    <p className={styles.loadingmessage}>로딩 중입니다...</p>
                ) : (
                    <div className={styles.itemsGrid}>
                        {recommendations.map((item) => (
                            <div key={item.itemId} className={styles.itemCard}>
                                <img
                                    src={item.itemImageUrl}
                                    alt={item.itemName}
                                    onClick={() => window.open(item.itemLink, "_blank")}
                                    style={{ cursor: "pointer" }}
                                />
                                <h3>{item.itemName}</h3>
                                <p>가격: {item.itemFinalPrice}원</p>
                                <button
                                    onClick={() => handleConfirmPurchase(item.itemId)}
                                    className={styles.purchaseButton}
                                >
                                    구매 확정
                                </button>
                            </div>
                        ))}
                    </div>
                    )}
            </div>

            {/* 통계 섹션 */}
            {statistics && (
                <div className={styles.statisticsSection}>
                    <div className={`${styles.statisticsCard} ${styles.purchaseStatistics}`}>
                        <div className={styles.statisticsGrid}>
                            {/* 구매 횟수 */}
                            <div className={styles.statisticsItem}>
                                <img
                                    src="https://koreansandwich-ecommerce-item-image.s3.ap-southeast-2.amazonaws.com/ecommerce-frontend/shopping-bag.png"
                                    alt="구매 횟수"
                                    className={styles.icon}
                                />
                                <div className={styles.statisticsText}>
                                    <p>구매 횟수</p>
                                    <h4>{statistics.purchaseCount}</h4>
                                </div>
                            </div>
                            {/* 리뷰 개수 */}
                            <div className={styles.statisticsItem}>
                                <img
                                    src="https://koreansandwich-ecommerce-item-image.s3.ap-southeast-2.amazonaws.com/ecommerce-frontend/pen.png"
                                    alt="리뷰 개수"
                                    className={styles.icon}
                                />
                                <div className={styles.statisticsText}>
                                    <p>리뷰 개수</p>
                                    <h4>{statistics.reviewCount}</h4>
                                </div>
                            </div>
                            {/* 평균 별점 */}
                            <div className={styles.statisticsItem}>
                                <img
                                    src="https://koreansandwich-ecommerce-item-image.s3.ap-southeast-2.amazonaws.com/ecommerce-frontend/star.png"
                                    alt="평균 별점"
                                    className={styles.icon}
                                />
                                <div className={styles.statisticsText}>
                                    <p>평균 별점</p>
                                    <h4>{statistics.averageRating.toFixed(2)}</h4>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className={styles.chartCard}>
                        <h3>구매한 제품 카테고리</h3>
                        <div className={styles.chartContainer}>
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
                                <Tooltip/>
                                <Legend
                                    layout="horizontal"
                                    align="center"
                                    verticalAlign="bottom"
                                    wrapperStyle={{fontSize: "12px"}}
                                />
                            </PieChart>
                        </div>
                    </div>

                    <div className={styles.chartCard}>
                        <h3>평균 별점 분포</h3>
                        <div className={styles.chartContainer}>
                            <BarChart
                                width={300}
                                height={200}
                                data={ratingData}
                                margin={{top: 20, right: 30, left: 20, bottom: 20}}
                            >
                                {/* X축 설정 */}
                                <XAxis
                                    dataKey="name"
                                    type="category"
                                    domain={["1점", "2점", "3점", "4점", "5점"]} // 1점 ~ 5점 고정
                                    tick={{fontSize: 12}}
                                    interval={0} // 모든 tick 표시
                                />
                                {/* Y축 설정 (숫자 숨김) */}
                                <YAxis
                                    tickFormatter={(value) => (Number.isInteger(value) ? value : '')} // 정수만 표시
                                    tick={{fontSize: 12}}
                                />
                                {/* 툴팁 */}
                                <Tooltip/>
                                {/* 데이터 바 */}
                                <Bar dataKey="value" fill="#82ca9d"/>
                            </BarChart>
                        </div>
                    </div>


                    <div className={styles.keywordCard}>
                        <h3>선호 키워드</h3>
                        <div className={styles.keywordGrid}>
                            {topKeywords.length > 0 ? (
                                topKeywords.map((keyword, index) => (
                                    <div key={index} className={styles.keywordItem}>
                                        <img
                                            src={`https://koreansandwich-ecommerce-item-image.s3.ap-southeast-2.amazonaws.com/ecommerce-frontend/${index + 1}st-place.png`}
                                            alt={`${index + 1}위`}
                                            className={styles.keywordIcon}
                                        />
                                        <div className={styles.keywordText}>
                                            <p>{index + 1}위</p>
                                            <h4>{keyword}</h4>
                                        </div>
                                    </div>
                                ))
                            ) : (
                                <p>데이터 없음</p>
                            )}
                        </div>
                    </div>

                </div>
            )}
        </div>
    );
};

export default DashBoard;
